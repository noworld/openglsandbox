precision mediump float;

const float c_Dec = 0.0;
const float c_LineW = 1.0; //gradient width
const float c_MaxGrad = 1.0;

uniform sampler2D u_Texture;
  
varying vec3 v_Position;
varying vec3 v_Normal;
varying vec2 v_TexCoordinate;

void main()                    		
{
	float r = cos(radians(c_Dec));
    float x = v_Position.x;
    float y = v_Position.y;
    
    //gl_FragColor = vec4(1.0,1.0,1.0,1.0);
    
    float c = r - ((x*x) + (y*y));
    
    if(c < c_LineW && c > -c_LineW) {
        gl_FragColor = vec4(0.6,0.8,1.0,c_MaxGrad-(sqrt(abs(c)/c_LineW)));
    }
    
    //gl_FragColor = texture2D(u_Texture, v_TexCoordinate);                     		
}                                                                     	

