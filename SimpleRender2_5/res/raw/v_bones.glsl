uniform mat4 u_MVPMatrix;	       
uniform mat4 u_MVMatrix;
//uniform mat4[20] u_Bones;
uniform mat4[20] u_Bones;
		  			
attribute vec4 a_Position; 							
attribute vec3 a_Normal; 
attribute vec2 a_TexCoordinate;	
attribute float a_BonesCount;

attribute vec3 a_BoneIndexOne; 
attribute vec3 a_BoneIndexTwo; 
attribute vec3 a_BoneWeightsOne; 
attribute vec3 a_BoneWeightsTwo; 	
		  
varying vec3 v_Position;     		          		
varying vec3 v_Normal;
varying vec2 v_TexCoordinate; 		
 
void main()                                                 	
{   
	vec4 bonePos = vec4(0.0);
	vec4 boneNrm = vec4(0.0);                                     	
	int count = int(a_BonesCount);
	
	if(count < 1) {
		bonePos = a_Position;
		boneNrm = vec4(a_Normal, 0.0);
	}
	
	for(int i = 0; i < count; i++) {
		int index;
		float weight;
		if(i == 0) {
			index = int(a_BoneIndexOne.x);
			weight = a_BoneWeightsOne.x;
		} else if(i == 1) {
			index = int(a_BoneIndexOne.y);
			weight = a_BoneWeightsOne.y;
		} else if(i == 2) {
			index = int(a_BoneIndexOne.z);
			weight = a_BoneWeightsOne.z;
		} else if(i == 3) {
			index = int(a_BoneIndexTwo.x);
			weight = a_BoneWeightsTwo.x;
		} else if(i == 4) {
			index = int(a_BoneIndexTwo.y);
			weight = a_BoneWeightsTwo.y;
		} else if(i == 5) {
			index = int(a_BoneIndexTwo.z);
			weight = a_BoneWeightsTwo.z;
		}

		//bonePos = (boneMat * a_Position) * weight + bonePos;
		//boneNrm = (boneMat * vec4(a_Normal,0.0)) * weight + boneNrm;
		
		if(index > -1) {
		    mat4 boneMat = u_Bones[index];
		    //bonePos = ((boneMat * weight) * a_Position) + bonePos;
			//boneNrm = ((boneMat * weight)) * vec4(v_Normal,0.0) + boneNrm;
			bonePos = ((boneMat * a_Position) * weight) + bonePos;
			boneNrm = ((boneMat * vec4(v_Normal,0.0)) * weight) + boneNrm;
		}
	}  

	//v_Position = vec3(u_MVMatrix * a_Position);       		
	//v_TexCoordinate = a_TexCoordinate;                                      
    //v_Normal = vec3(u_MVMatrix * vec4(a_Normal, 0.0));
	//gl_Position =  u_MVPMatrix * a_Position;     
	       
	v_Position = vec3(u_MVMatrix * bonePos);  
	v_TexCoordinate = a_TexCoordinate;
	v_Normal = vec3(u_MVMatrix * boneNrm);
	gl_Position = u_MVPMatrix * bonePos;

}                                                          
