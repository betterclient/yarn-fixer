# Yarn Fixer

## Made for people who wants to use yarn mappings for a minecraft client
## It also Crashes explorer if you go to the %temp% folder
When you use yarn to get named mappings and decompile them with jd-gui it creates non wanted lines. For Example:
@Environment(EnvType.CLIENT) //Created By Yarn
/*      */  //Created by jd-gui

This Program Retrieves a .zip file containing yarn named that is decompiled with jd-gui and fixes them!

# Building
Get jdk 1.9+ and build it like any other project

