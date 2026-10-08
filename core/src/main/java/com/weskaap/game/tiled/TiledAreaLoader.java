package com.weskaap.game.tiled;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * Loads a Tiled {@code .tmx} file into {@link AreaData}.
 *
 * <p>The loader deliberately avoids runtime dependencies on libGDX file handles or
 * the Tiled map loader; it parses the TMX XML directly so that it can be unit
 * tested with classpath resources and reused in headless contexts.
 *
 * <p>M12 scope is limited to extracting the data that actually exists in the TMX:
 * map metadata, collision rectangles, generic objects, and spawn points. Full
 * building synthesis and runtime wiring are intentionally left for M13.
 */
public final class TiledAreaLoader {

    private static final String LAYER_COLLISION = "Collision";
    private static final String LAYER_OBJECTS = "Objects";
    private static final String LAYER_SPAWNS = "Spawns";
    private static final String OBJECT_TYPE_BUILDING = "building";

    private final float scale;

    public TiledAreaLoader() {
        this(2.0f);
    }

    public TiledAreaLoader(float scale) {
        if (scale <= 0f) {
            throw new IllegalArgumentException("Scale must be positive");
        }
        this.scale = scale;
    }

    public float getScale() {
        return scale;
    }

    /**
     * Loads the named TMX file from the classpath and returns the parsed data.
     *
     * @param tmxPath classpath path to the TMX file, e.g. {@code "retreat_location.tmx"}
     * @return parsed area data
     * @throws IllegalArgumentException if the file cannot be found or parsed
     */
    public AreaData load(String tmxPath) {
        try (InputStream stream = getClass().getClassLoader().getResourceAsStream(tmxPath)) {
            if (stream == null) {
                throw new IllegalArgumentException("TMX file not found on classpath: " + tmxPath);
            }
            return load(stream, tmxPath);
        } catch (IOException e) {
            throw new IllegalArgumentException("Failed to read TMX file: " + tmxPath, e);
        }
    }

    public AreaData load(InputStream inputStream, String sourceName) {
        Document document = parseDocument(inputStream, sourceName);
        Element root = document.getDocumentElement();

        int mapWidthTiles = parseIntAttribute(root, "width");
        int mapHeightTiles = parseIntAttribute(root, "height");
        float tileWidth = parseFloatAttribute(root, "tilewidth");
        float tileHeight = parseFloatAttribute(root, "tileheight");

        float mapPixelWidth = mapWidthTiles * tileWidth;
        float mapPixelHeight = mapHeightTiles * tileHeight;
        TiledCoordinateConverter converter = new TiledCoordinateConverter(mapPixelWidth, mapPixelHeight, scale);

        String areaName = root.getAttribute("name");
        if (areaName == null || areaName.isBlank()) {
            areaName = sourceName;
        }
        String areaId = toAreaId(areaName);

        List<Rectangle> collisionBounds = new ArrayList<>();
        List<MapObjectData> objects = new ArrayList<>();
        List<BuildingData> buildings = new ArrayList<>();
        List<SpawnData> spawns = new ArrayList<>();
        List<TileLayerData> tileLayers = new ArrayList<>();

        NodeList layerNodes = root.getChildNodes();
        for (int i = 0; i < layerNodes.getLength(); i++) {
            Node node = layerNodes.item(i);
            if (node.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }
            Element element = (Element) node;
            String tag = element.getTagName();
            String name = element.getAttribute("name");
            if ("objectgroup".equals(tag) && LAYER_COLLISION.equals(name)) {
                collisionBounds.addAll(parseCollisionObjects(element, converter));
            } else if ("objectgroup".equals(tag) && LAYER_OBJECTS.equals(name)) {
                List<MapObjectData> parsed = parseGenericObjects(element, converter);
                objects.addAll(parsed);
                for (MapObjectData object : parsed) {
                    if (OBJECT_TYPE_BUILDING.equalsIgnoreCase(object.getType())) {
                        buildings.add(toBuildingData(object));
                    }
                }
            } else if ("objectgroup".equals(tag) && LAYER_SPAWNS.equals(name)) {
                spawns.addAll(parseSpawnObjects(element, converter));
            } else if ("layer".equals(tag)) {
                tileLayers.add(parseTileLayer(element));
            }
        }

        return new AreaData(
            areaId,
            areaName,
            tileWidth,
            tileHeight,
            mapWidthTiles,
            mapHeightTiles,
            converter.getWorldWidth(),
            converter.getWorldHeight(),
            collisionBounds,
            buildings,
            objects,
            spawns,
            tileLayers);
    }

    private TileLayerData parseTileLayer(Element layerElement) {
        String name = layerElement.getAttribute("name");
        int width = parseIntAttribute(layerElement, "width");
        int height = parseIntAttribute(layerElement, "height");
        NodeList dataNodes = layerElement.getElementsByTagName("data");
        if (dataNodes.getLength() == 0) {
            throw new IllegalArgumentException("Tile layer '" + name + "' has no <data> element");
        }
        Element dataElement = (Element) dataNodes.item(0);
        String encoding = dataElement.getAttribute("encoding");
        if (encoding != null && !encoding.isBlank() && !"csv".equals(encoding)) {
            throw new IllegalArgumentException(
                "Tile layer '" + name + "' uses unsupported encoding: " + encoding);
        }
        String csv = dataElement.getTextContent();
        int[] tiles = parseCsvTiles(csv, width * height, name);
        return new TileLayerData(name, width, height, tiles);
    }

    private int[] parseCsvTiles(String csv, int expectedCount, String layerName) {
        String[] tokens = csv.split(",");
        int[] tiles = new int[expectedCount];
        int index = 0;
        for (String token : tokens) {
            String trimmed = token.trim();
            if (trimmed.isEmpty()) {
                continue;
            }
            if (index >= expectedCount) {
                throw new IllegalArgumentException(
                    "Tile layer '" + layerName + "' contains more tiles than " + expectedCount);
            }
            try {
                tiles[index++] = Integer.parseInt(trimmed);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException(
                    "Tile layer '" + layerName + "' contains a non-numeric gid: " + trimmed, e);
            }
        }
        if (index != expectedCount) {
            throw new IllegalArgumentException(
                "Tile layer '" + layerName + "' contains " + index
                    + " tiles, expected " + expectedCount);
        }
        return tiles;
    }

    private Document parseDocument(InputStream stream, String sourceName) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(false);
            factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
            factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
            factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
            DocumentBuilder builder = factory.newDocumentBuilder();
            return builder.parse(stream);
        } catch (ParserConfigurationException | SAXException | IOException e) {
            throw new IllegalArgumentException("Failed to parse TMX file: " + sourceName, e);
        }
    }

    private List<Rectangle> parseCollisionObjects(Element objectGroup, TiledCoordinateConverter converter) {
        List<Rectangle> result = new ArrayList<>();
        NodeList nodes = objectGroup.getElementsByTagName("object");
        for (int i = 0; i < nodes.getLength(); i++) {
            Element object = (Element) nodes.item(i);
            float x = parseFloatAttribute(object, "x");
            float y = parseFloatAttribute(object, "y");
            float width = parseFloatAttribute(object, "width");
            float height = parseFloatAttribute(object, "height");
            result.add(converter.toWorldRectangle(x, y, width, height));
        }
        return result;
    }

    private List<MapObjectData> parseGenericObjects(Element objectGroup, TiledCoordinateConverter converter) {
        List<MapObjectData> result = new ArrayList<>();
        NodeList nodes = objectGroup.getElementsByTagName("object");
        for (int i = 0; i < nodes.getLength(); i++) {
            Element object = (Element) nodes.item(i);
            result.add(parseObjectData(object, converter));
        }
        return result;
    }

    private List<SpawnData> parseSpawnObjects(Element objectGroup, TiledCoordinateConverter converter) {
        List<SpawnData> result = new ArrayList<>();
        NodeList nodes = objectGroup.getElementsByTagName("object");
        for (int i = 0; i < nodes.getLength(); i++) {
            Element object = (Element) nodes.item(i);
            String id = parseObjectId(object);
            String name = object.getAttribute("name");
            String type = object.getAttribute("type");
            float x = parseFloatAttribute(object, "x");
            float y = parseFloatAttribute(object, "y");
            float width = parseFloatAttribute(object, "width");
            float height = parseFloatAttribute(object, "height");
            Vector2 position = converter.toWorldPosition(x, y, width, height);
            result.add(new SpawnData(id, name, type, position));
        }
        return result;
    }

    private MapObjectData parseObjectData(Element object, TiledCoordinateConverter converter) {
        String id = parseObjectId(object);
        String name = object.getAttribute("name");
        String type = object.getAttribute("type");
        float x = parseFloatAttribute(object, "x");
        float y = parseFloatAttribute(object, "y");
        float width = parseFloatAttribute(object, "width");
        float height = parseFloatAttribute(object, "height");
        Rectangle bounds = converter.toWorldRectangle(x, y, width, height);
        Vector2 position = new Vector2(bounds.x + bounds.width / 2f, bounds.y + bounds.height / 2f);
        Map<String, String> properties = parseProperties(object);
        return new MapObjectData(id, name, type, bounds, position, properties);
    }

    private BuildingData toBuildingData(MapObjectData object) {
        return new BuildingData(
            object.getId(),
            object.getName(),
            object.getBounds(),
            object.getPosition(),
            object.getProperty("interiorId", ""),
            object.getProperties());
    }

    private Map<String, String> parseProperties(Element object) {
        Map<String, String> result = new HashMap<>();
        NodeList propertyGroups = object.getElementsByTagName("properties");
        for (int i = 0; i < propertyGroups.getLength(); i++) {
            Element group = (Element) propertyGroups.item(i);
            NodeList properties = group.getElementsByTagName("property");
            for (int j = 0; j < properties.getLength(); j++) {
                Element property = (Element) properties.item(j);
                String name = property.getAttribute("name");
                String value = property.getAttribute("value");
                if (name != null && !name.isBlank()) {
                    result.put(name, value == null ? "" : value);
                }
            }
        }
        return result;
    }

    private String parseObjectId(Element object) {
        String id = object.getAttribute("id");
        return id == null || id.isBlank() ? "0" : id;
    }

    private int parseIntAttribute(Element element, String name) {
        String value = element.getAttribute(name);
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Missing required attribute '" + name + "' on " + element.getTagName());
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Attribute '" + name + "' is not an integer: " + value, e);
        }
    }

    private float parseFloatAttribute(Element element, String name) {
        String value = element.getAttribute(name);
        if (value == null || value.isBlank()) {
            return 0f;
        }
        try {
            return Float.parseFloat(value);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Attribute '" + name + "' is not a float: " + value, e);
        }
    }

    private String toAreaId(String name) {
        return name.trim().toLowerCase().replaceAll("[^a-z0-9]+", "_").replaceAll("_+$", "");
    }
}
