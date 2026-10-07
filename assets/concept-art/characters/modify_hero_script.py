#!/usr/bin/env python3
# MakeHuman scripting helper for hero_3D_concept_idea
# Run via: C:\Users\lorean.devries\AppData\Local\makehuman-community\Python\pythonw.exe \
#             C:\Users\lorean.devries\AppData\Local\makehuman-community\mhstartwrapper.py
# Then load this script in MakeHuman's Scripting plugin (Plugins -> Scripting) and run,
# or use MHScript module inside MakeHuman GUI context.

import MHScript

# Load hero model (assumes .mhm in same folder or models dir)
# For Blender-origin .blend hero: export to .mhm first, or import via MakeHuman Blender plugin.
MHScript.loadModel('hero_3D_concept_idea', 'C:/Users/lorean.devries/Documents/playground/weskaap/assets/concept-art/characters')

# Apply stylized modifications (example targets / parameters)
# MHScript.applyTarget('body/breast-volume', 0.2)  # adjust if targets exist
# MHScript.setAge(25)
# MHScript.setWeight(0.5)

# Save back / export for Blender use
MHScript.saveModel('hero_3D_concept_idea_modified')
MHScript.saveObj('hero_3D_concept_idea_modified.obj')
print("Hero modified and saved to characters folder.")
