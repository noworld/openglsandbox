#VBO/IBO/DSC Model Writing from Blender
#Custom Properties:
#scene.hzg_file_name - name of zip file to write
#object.hzg_type - type of object to be populated by the reading program
#    - Defaults to "ENTITY"
#    - GEO_MIPMAP - Used for terrain geometry
#   - UI_ELEMENT - Drawn in the same position in front of the camera as UI elements
#   - INPUT_AREA - Polygons drawn over UI area to detect input 
#   - SKYDOME - Skydome
#object.hzg_export_mode - how to export the data for this object
#    - V   - Write tightly packed vertex position data, no normals or texture coordinates (VVV)
#    - VC  - Write Vertex position and Texture Coordinates in VVVCC format.
#    - VNC - Write packed Vertex/Normal/TexCoord data in VVVNNNCC format
#    - VNCB - Write packed Vertex/Normal/TexCoord/Bone data in VVVNNNCCIxWx format
#            *x will be max number of bone weights to apply to a vertex
#object.hzg_round - integer, how many places to round decimals
#object.hzg_texture - texture name to use
#object.hzg_shader - shader name to use
#object.hzg_command - command to link to an input area
#object.hzg_z_offset - Z offset adjustments for tuning UI controls
#object.hzg_transform_yup - 1 means that the object should be transformed from Z-Up coordinates to Y-Up coordinates
#object.hzg_bone_weight_count - The max number of bone weights to apply to a vertex

import bpy
import bmesh
import zipfile
import struct
import sys
import os
import math
import mathutils

try:
    import zlib
    compression = zipfile.ZIP_DEFLATED
except:
    compression = zipfile.ZIP_STORED
bpf = 4
bpi = 4
bps = 2
clear_work_directory = True
home_path = "C:" + os.environ.get("HOMEPATH","\\Users\\nicholas.waun")
proj_path = home_path + "\\git\\openglsandbox\\SimpleRender2_5"
out_path = proj_path + "\\res\\raw\\"
work_path = proj_path + "\\python\\work\\"
dsc_ext = ".dsc"
vbo_ext = ".v"
ibo_ext = ".i"
arm_ext = ".arm"
game_prefix = "hzg_"
default_bone_weight_count = 6
armatures = {}
rot_y_up = mathutils.Matrix.Rotation(math.radians(90.0),4,'X')
    
def cross_z(p1, p2, p3):
    return ((p2.x-p1.x) * (p3.y-p1.y)) - ((p2.y-p1.y) * (p3.x-p1.x));
    
def write_armature_pose_files(obj, scene, childMesh):
    file_name = obj.name
    print("File name is: ",file_name)
    scene.objects.active = obj
    arm = obj.data
    armDesc = ""
    poseNames = ""
    bone_indexes = {}
    armDesc += "ARMATURE=%s\n" % arm.name
    
    for i in range(len(obj.pose_library.pose_markers)):
        if(poseNames != ""):
            poseNames += ","        
        poseName =  obj.pose_library.pose_markers[i].name
        poseNames += poseName
        bpy.ops.poselib.apply_pose(pose_index=i)
        poseStr = ""
        for j in range(len(arm.bones)):
            if(poseStr != ""):
                poseStr += ","
            boneMatrix = (obj.pose.bones[j].matrix)
            print("Bone: ",obj.pose.bones[j].name)
            boneMatrix = boneMatrix.transposed() * rot_y_up
            bone_matrix_str = str(boneMatrix).replace("\n","").replace("<Matrix 4x4 ","")       
            bone_matrix_str = bone_matrix_str.replace(">","").replace(" ","").replace(")(",",")
            bone_matrix_str = bone_matrix_str.replace("(","").replace(")","") 
            poseStr += bone_matrix_str
        armDesc += "%s=%s\n" % (poseName,poseStr)
        
    #armDesc += "POSE_NAMES=%s\n" % poseNames
    
    restPoseStr = ""
    restPoseInvStr = ""
    for i in range(len(arm.bones)):
        bone = arm.bones[i]
        print("Rest Bone: ",arm.bones[i].name)
        bone_indexes[bone.name] = i
        if(restPoseStr != ""):
            restPoseStr += ","
            restPoseInvStr += ","   
                 
        boneBindMatrixTransp = (bone.matrix_local.transposed()) * rot_y_up
        boneBindMatrixTranspInv = boneBindMatrixTransp.inverted()
        
        #boneBindMatrixTransp = bone.matrix_local.transposed() * rot_y_up
        bone_bind_str = str(boneBindMatrixTransp).replace("\n","").replace("<Matrix 4x4 ","")      
        bone_bind_str = bone_bind_str.replace(">","").replace(" ","").replace(")(",",")
        bone_bind_str = bone_bind_str.replace("(","").replace(")","")
        restPoseStr += bone_bind_str
        #boneBindMatrixTranspInv = boneBindMatrixTransp.inverted()
        bone_inv_bind_str = str(boneBindMatrixTranspInv).replace("\n","").replace("<Matrix 4x4 ","")      
        bone_inv_bind_str = bone_inv_bind_str.replace(">","").replace(" ","").replace(")(",",")
        bone_inv_bind_str = bone_inv_bind_str.replace("(","").replace(")","")
        restPoseInvStr += bone_inv_bind_str
        print("\n----------- BONE: %s -----------" % bone.name)
        print("BONE NAME %s // PBONE NAME %s" % (bone.name, obj.pose.bones[i].name))     
        print("Is deform enabled:",bone.use_deform)
        print("Rot Y UP Matrix:              %s" % rot_y_up)
        print("Bind Matrix:                  %s" % boneBindMatrixTranspInv)
        print("Pose Matrix:                  %s" % boneMatrix)
       
        print("--------------------------------\n")
        
    armDesc += "Rest=%s\n" % restPoseStr
    armDesc += "RestInv=%s\n" % restPoseInvStr

    
    root_bone = None
    found = False
    for i in range(0,len(arm.bones)):
        bone = arm.bones[i]
        if(bone.parent is None and not found):
            root_bone = bone
            print("Root bone:", bone.name)
            found = True
            
    fileArm = open(work_path + file_name + arm_ext, 'w')
    fileArm.write(armDesc)
    fileArm.close()

    armatures[obj.name] = bone_indexes;
    
    return file_name
    
def write_armature_files(obj, scene, childMesh):
    file_name = obj.name
    print("File name is: ",file_name)
    scene.objects.active = obj
    arm = obj.data
    armDesc = ""
    poseNames = ""
    bone_indexes = {}
    armDesc += "ARMATURE=%s\n" % arm.name
    
    root_bone = None
    found = False
    for i in range(0,len(arm.bones)):
        bone = arm.bones[i]
        if(bone.parent is None and not found):
            root_bone = bone
            print("Root bone:", bone.name)
            found = True
    
    bone_combined_mats = {}
    for i in range(0,len(arm.bones)):
        bone = arm.bones[i]
        print("Bone %s has %s parents." % (bone.name,len(bone.parent_recursive)))
        combined_mat = mathutils.Matrix.Identity(4)
        for j in range(0,len(bone.parent_recursive)):
            combined_mat = combined_mat * bone.parent_recursive[j].matrix_local.transposed()
        bone_combined_mats[i] = combined_mat            


    for i in range(0,len(arm.bones)):
        bone = arm.bones[i]
        parentName = "None"
        root_bone = False
        if(bone.parent is not None):
            parentName = bone.parent.name
        else:
            root_bone = True

        boneMatrix = (obj.pose.bones[i].matrix) # * rot_y_up
        boneMatrix = boneMatrix.transposed() * rot_y_up

        boneBindMatrixTransp = (bone.matrix_local.transposed()) * rot_y_up
        ### THIS ONE SETS TO IDENTITY??
        boneBindMatrixTranspInv = boneBindMatrixTransp.inverted()
        #boneBindMatrixTranspInv = bone_combined_mats[i].inverted()
        
        bindinv_matrix = boneBindMatrixTransp * boneBindMatrixTranspInv
        bonebind_matrix = boneMatrix * boneBindMatrixTranspInv
        
        parent_matrix = "None"
        parent_bind_matrix = "None" 
        bone_parent_inverted_matrix = "None"
        if(bone.parent is not None):
            parent_matrix = obj.pose.bones[i].parent.matrix.transposed()
            parent_bind_matrix = bone.parent.matrix_local.transposed().inverted()
            bone_parent_inverted_matrix = boneMatrix * parent_matrix.inverted()
            #boneMatrix = bone_parent_inverted_matrix
        
        bone_euler = boneMatrix.to_euler('XYZ')        
        bind_euler = boneBindMatrixTransp.to_euler('XYZ')
        inv_bind_euler = boneBindMatrixTranspInv.to_euler('XYZ')

        print("\n----------- BONE: %s -----------" % bone.name)
        print("BONE NAME %s // PBONE NAME %s" % (bone.name, obj.pose.bones[i].name))
        print("Is root bone:",root_bone)        
        print("Is deform enabled:",bone.use_deform)
        print("Rot Y UP Matrix:              %s" % rot_y_up)
        print("Accessing [Y] matrix element: %s" % boneBindMatrixTransp[3][1])
        print("Accessing [Z] matrix element: %s" % boneBindMatrixTransp[3][2])    
        print("Head      :                   %s" % bone.head)
        print("Head local:                   %s" % bone.head_local)
        print("Pose Head:                    %s" % obj.pose.bones[i].head)
        obj.pose.bones[i]
        print("Bind Matrix:                  %s" % boneBindMatrixTranspInv)
        print("Pose Matrix:                  %s" % boneMatrix)
        print("Pose Tx:                      %s" % boneMatrix.to_translation())
        print("Bind*BindInv Matrix:          %s" % bindinv_matrix)
        print("Pose*BindInv Matrix:          %s" % bonebind_matrix)
        print("Bone Combined Matrix:         %s" % bone_combined_mats[i])
        print("Bone Matrix Euler:            %s" % bone_euler)
        print("Bone Bind Matrix Euler:       %s" % bind_euler)
        print("Inv Bone Bind Matrix Euler:   %s" % inv_bind_euler)
        print("\n")
        print("Parent Matrix:                %s" % parent_matrix)
        print("Parent Bind Matrix:           %s" % parent_bind_matrix)
        print("Bone*Parent Matrix:           %s" % bone_parent_inverted_matrix)
        print("--------------------------------\n")
        
        bone_matrix_str = str(boneMatrix).replace("\n","").replace("<Matrix 4x4 ","")       
#        bone_matrix_str = str(bone.matrix_local).replace("\n","").replace("<Matrix 4x4 ","")
#        bone_matrix_str = str(bone.matrix).replace("\n","").replace("<Matrix 3x3 ","")
        bone_matrix_str = bone_matrix_str.replace(">","").replace(" ","").replace(")(",",")
        bone_matrix_str = bone_matrix_str.replace("(","").replace(")","")   
        
        bone_bind_str = str(boneBindMatrixTransp).replace("\n","").replace("<Matrix 4x4 ","")    
        bone_bind_str = bone_bind_str.replace(">","").replace(" ","").replace(")(",",")
        bone_bind_str = bone_bind_str.replace("(","").replace(")","")
        
        bone_inv_bind_str = str(boneBindMatrixTranspInv).replace("\n","").replace("<Matrix 4x4 ","")      
        bone_inv_bind_str = bone_inv_bind_str.replace(">","").replace(" ","").replace(")(",",")
        bone_inv_bind_str = bone_inv_bind_str.replace("(","").replace(")","")
        
        head = obj.pose.bones[i].head
        bone_head_str = "%s,%s,%s" % (head.x,head.z,-head.y)

        armDesc += "%s=%i,%s\n" % (bone.name, i, parentName)
        armDesc += "%s_MATRIX=%s\n" % (bone.name,bone_matrix_str)
        armDesc += "%s_HEAD=%s\n" % (bone.name,bone_head_str)
        armDesc += "%s_BIND=%s\n" % (bone.name,bone_bind_str)
        armDesc += "%s_INVBIND=%s\n" % (bone.name,bone_inv_bind_str)
        armDesc += "%s_IS_ROOT=%s\n" % (bone.name,root_bone)
        bone_indexes[bone.name] = i
        
    for i in range(0,len(obj.pose.bones)):
        p_bone = obj.pose.bones[i]
        print("Pose bone %s: %s" % (i, p_bone.name))
        print("HEAD:",p_bone.head)
        print("LOCATION:",p_bone.location)
        print("ROTATION:",p_bone.rotation_euler)
        print("MATRIX:",p_bone.matrix)
        print("MATRIX_BASIS:",p_bone.matrix_basis)
        print("\n")
        
    
    fileArm = open(work_path + file_name + arm_ext, 'w')
    fileArm.write(armDesc)
    fileArm.close()

    armatures[obj.name] = bone_indexes;
    
#    fileIdx = open(work_path + "index", 'a')
#    fileIdx.write("+%s\n" % (file_name + arm_ext))
#    fileIdx.close()
    
    return file_name

def write_mesh_files(obj, scene):
    max_vert_group_count = 0
    scene.objects.active = obj
    file_name = obj.name
    trans_yup = obj.get("hzg_transform_yup",1)
    vert_groups = obj.vertex_groups
    export_mode = obj.get("hzg_export_mode","VNC")
    bone_weight_count = obj.get("hzg_bone_weight_count",default_bone_weight_count)
    z_offset = obj.get("hzg_z_offset",0.0)
    print("HZG_EXPORT_MODE is",export_mode)
    bpy.ops.object.modifier_apply(modifier='Subsurf')
    round_verts = obj.get("hzg_round",0)
    bm = bmesh.new()
    bm.from_mesh(obj.data)
    bmesh.ops.triangulate(bm, faces=bm.faces)
    print("*** NUM FACES:",len(bm.faces))
    print("*** ROUND VERTEXES:",round_verts)
    #print("MESH VOLUME:",bm.calc_volume(False))
    
    uv_lay = bm.loops.layers.uv.active #This will be None if object is not unwrapped
    print("Active layer:",uv_lay)
   
    if(uv_lay is None or export_mode == "V"):
        print("UV coords will not be exported for",obj.name)
        nrm_offset = 3 * bpf
        txc_offset = -1
        stride = 6 * bpf
    elif(export_mode == "VC"):
        print("Normals will not be exported for",obj.name)
        nrm_offset = -1
        txc_offset = 3 * bpf
        stride = 5 * bpf
    elif(export_mode == "VNCB"):
        #print("Bones will be exported for",obj.name)
        nrm_offset = 3 * bpf
        txc_offset = 6 * bpf
        bones_count_offset = 8 * bpf
        bones_index_offset = 9 * bpf
        bones_weights_offset = (9 * bpf) + (bone_weight_count * bpf)
        stride = (9 * bpf) + (bone_weight_count * bpf * 2)
        #stride = 9 * bpf
    else:
        print("Exporting data in VNC format for",obj.name)
        nrm_offset = 3 * bpf
        txc_offset = 6 * bpf
        stride = 8 * bpf #3+3+2
        
    vboBytes = bytearray()
    iboBytes = bytearray()
    dscString = "OBJECT_NAME=%s" % obj.name
    dscString += "\nOBJECT_TYPE=%s" % obj.get("hzg_type","ENTITY")
    dscString += "\nTEXTURE=%s" % obj.get("hzg_texture","uvgrid")
    dscString += "\nSHADER=%s" % obj.get("hzg_shader","tex_shader")
    dscString += "\nNUM_ELEMENTS=%i" % (len(bm.faces)*3)
    dscString += "\nPOS_OFFSET=0"
    dscString += "\nNRM_OFFSET=%i" % nrm_offset
    dscString += "\nTXC_OFFSET=%i" % txc_offset
    dscString += "\nELEMENT_STRIDE=%i" % stride
    dscString += "\nNRM_SIZE=3"
    dscString += "\nPOS_SIZE=3"
    dscString += "\nTXC_SIZE=2"
    dscString += "\nZ_OFFSET=%d" % z_offset
    if(export_mode == "VNCB"):
        dscString += "\nMAX_BONE_WEIGHT_COUNT=%i" % bone_weight_count
        dscString += "\nB_CT_SIZE=%i" % 1
        dscString += "\nB_ID_SIZE=%i" % bone_weight_count
        dscString += "\nB_WT_SIZE=%i" % bone_weight_count
        dscString += "\nB_CT_OFFSET=%i" % bones_count_offset
        dscString += "\nB_ID_OFFSET=%i" % bones_index_offset
        dscString += "\nB_WT_OFFSET=%i" % bones_weights_offset
        dscString += "\nARMATURE_NAME=%s" % obj.parent.pose_library.name
        dscString += "\nBONES=true"
    
    objType = obj.get("hzg_type","ENTITY")
    print("Object type for %s is %s" % (obj.name, objType))
    if(objType == "INPUT_AREA"):
        print("Generating convex hull for %s" % obj.name)
        hull_points= ""
        
        print("VERTS")
        vert_ctr = 0
        for vert in bm.verts:
            if(hull_points):
                hull_points = hull_points + ","
            point_string = ("%f,%f,%f") % (vert.co.x, vert.co.y, vert.co.z)
            hull_points = hull_points + point_string
        
        dscString += "\nHULL=%s" % hull_points
        dscString += "\nCOMMAND=%s" % obj.get("hzg_command","")
    
    print("**** STRIDE IS:",stride)
    ctr = 0
    for face in bm.faces:
#        print("**** Face #",face.index)
        for loop in face.loops:
#            print("**** Loop vert #",loop.vert.index)

            #Bones!        
            if(export_mode == "VNCB"):    
                parent = obj.parent
    #            print("Parent is:",parent.name)
                bone_indexes = armatures[parent.name]
                vert_obj = obj.data.vertices[loop.vert.index] #VertexGroupElement(bpy_struct)
                vert_grp_indexes = []
                vert_grp_weights = []
                if(len(vert_obj.groups) > max_vert_group_count):
                    max_vert_group_count = len(vert_obj.groups)
                
                vert_grp_count = len(vert_obj.groups);
                for i in range(0,bone_weight_count):
                    if(i < vert_grp_count):
                        bone_name = vert_groups[vert_obj.groups[i].group].name
                        vert_grp_indexes.append(bone_indexes[bone_name])
                        vert_grp_weights.append(vert_obj.groups[i].weight)
#                        print("Vertex is in group: %s (index: %i)" % (bone_name,bone_indexes[bone_name]))
                    else:
                        vert_grp_indexes.append(-1)
                        vert_grp_weights.append(0.0)
       
            pos = bm.verts[loop.vert.index].co
#            print("**** Vert: %.5f,%.5f,%.5f" % (pos.x,pos.y,pos.z))
            if(round_verts):
                if(trans_yup == 1):
                    vboBytes += struct.pack(">fff", round(pos.x,5), round(pos.z,5), -round(pos.y,5))
                else:
                    vboBytes += struct.pack(">fff", round(pos.x,5), round(pos.y,5), round(pos.z,5))
            else:
                if(trans_yup == 1):
                    vboBytes += struct.pack(">fff", pos.x,pos.z,-pos.y)
                else:
                    vboBytes += struct.pack(">fff", pos.x,pos.y,pos.z)
            
            if(export_mode == "VNC" or export_mode == "VNCB"):
                nrm = bm.verts[loop.vert.index].normal
#                print("**** Normal: %.4f,%.4f,%.4f" % (nrm.x, nrm.y, nrm.z))
                if(trans_yup == 1):
                    vboBytes += struct.pack(">fff",nrm.x,nrm.z,-nrm.y)
                else:
                    vboBytes += struct.pack(">fff",nrm.x,nrm.y,nrm.z)
            
            if(uv_lay is not None and export_mode != "V"):
                txc = loop[uv_lay].uv
#                print("**** Texture Coords:", txc.x, ",", txc.y)
                vboBytes += struct.pack(">ff", txc.x, 1-txc.y)
                
            #Bones!
            if(export_mode == "VNCB"):
#                print("Exporting mesh with bone weights. Count is:",vert_grp_count)
                vboBytes += struct.pack(">f", vert_grp_count)
                for i in range(0,bone_weight_count):
#                    print("Bone index: %f" % vert_grp_indexes[i])
                    vboBytes += struct.pack(">f", vert_grp_indexes[i])
                for i in range(0,bone_weight_count):
 #                   print("Bone weight: %f" % vert_grp_weights[i])
                    vboBytes += struct.pack(">f", vert_grp_weights[i])
            
            iboBytes += struct.pack(">h",ctr)
            ctr += 1
    bm.free()
    
    print("Max Vertex Groups Encountered:",max_vert_group_count)
    
    fileVbo = open(work_path + file_name + vbo_ext, 'wb')
    fileVbo.write(vboBytes)
    fileVbo.close()
    
    fileIbo = open(work_path + file_name + ibo_ext, 'wb')
    fileIbo.write(iboBytes)
    fileIbo.close()
    
    fileDsc = open(work_path + file_name + dsc_ext, 'w')
    fileDsc.write(dscString)
    fileDsc.close()
    
    idxString = "+" + file_name + "\n"
    idxString += "-" + file_name + vbo_ext + "\n"
    idxString += "-" + file_name + ibo_ext + "\n"
    idxString += "-" + file_name + dsc_ext + "\n"
    
    fileIdx = open(work_path + "index", 'a')
    fileIdx.write(idxString)
    fileIdx.close()
    
    bm.free()
    return file_name
#**********************************************************
print("\n*** Welcome to Blender Python Zip Exporter. ***\n");
print("Running in",sys.argv[0])
print(sys.version_info);
print("")
fileIdx = open(work_path + "index", 'w')
fileIdx.write("")
fileIdx.close()
for scene in bpy.data.scenes:
        print("* Operating on Scene:",scene.name)
        print("* Using zip file",scene["hzg_file_name"],"\n")
        zf = zipfile.ZipFile(out_path + scene["hzg_file_name"], mode='w')
                
        #handle the rest of the scene
        for obj in scene.objects:
            if(obj.type == "MESH" and obj.is_visible(scene)):
                
                #write armature
                if(obj.parent.type == "ARMATURE"):
                    print("*** Writing ",obj.parent.name,"type is",obj.parent.type,"visibility is",obj.parent.is_visible(scene))
                    file_name = write_armature_pose_files(obj.parent, scene, obj)
                    zf.write(work_path + file_name + arm_ext, file_name + arm_ext, compress_type=compression)
                
                print("** Writing",obj.name)
                file_name = write_mesh_files(obj, scene)  
                zf.write(work_path + file_name + dsc_ext, file_name + dsc_ext, compress_type=compression)   
                zf.write(work_path + file_name + vbo_ext, file_name + vbo_ext, compress_type=compression)
                zf.write(work_path + file_name + ibo_ext, file_name + ibo_ext, compress_type=compression)
                print("")
            else:
                if(obj.type == "ARMATURE"):
                    print("Armatures are written as parents of animated objects: ",obj.name)
                else:
                    print("Not including",obj.name,"type is",obj.type,"visibility is",obj.is_visible(scene))
        zf.write(work_path + "index", "index", compress_type=compression)
        zf.close()
if(clear_work_directory):
    print("not implemented")
    #print(mathutils.Quaternion((0.89, -0.455, -0.025, 0.013)).to_matrix().transposed())
    print(mathutils.Quaternion((0.89, -0.455, 0.013, 0.025)).to_matrix().to_4x4().transposed())
