precision mediump float;

uniform samplerCube u_Texture;
uniform mat4 u_IVMatrix;
  
varying vec3 v_Position;
varying vec3 v_Normal;
//varying vec2 v_TexCoordinate;

void main()                    		
{                           

    vec3 incident_eye = normalize(v_Position);
    vec3 normal = normalize(v_Normal);

    vec3 reflected = reflect(incident_eye, normal);
    reflected = vec3(u_IVMatrix * vec4(reflected, 0.0));

    gl_FragColor = textureCube(u_Texture, reflected);
                      		
}                                                                     	

