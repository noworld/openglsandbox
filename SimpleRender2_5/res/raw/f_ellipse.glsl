precision mediump float;

struct ellipse {
	float min;
	float maj;
	float minsq;
	float majsq;
	vec2  loc;
	float rfocsq;
};

const int MAX_ELLIPSES = 15;

uniform sampler2D u_Texture;
uniform int u_NumEllipses;
uniform ellipse[MAX_ELLIPSES] u_Ellipses;
uniform vec4 u_WaterColor;
uniform vec4 u_LandColor;

varying vec2 v_TexCoordinate;  
varying vec3 v_Position;
  
void main()                    		
{                           
	for(int i = 0; i < u_NumEllipses; i++) {
		ellipse ell = u_Ellipses[i];
		
		float xh = v_Position.x - ell.loc.x;
		float yk = v_Position.y - ell.loc.y;				
		float rx = ell.majsq;
		float ry = ell.minsq;
		
		//float xh = v_Position.x - 0.0;
		//float yk = v_Position.y - 0.0;				
		//float rx = 1.0;
		//float ry = 1.0;
		
		if( ((xh*xh)/(rx)) + ((yk*yk) / (ry))  <= 1.0) {
			gl_FragColor = u_LandColor;
		} else {
			gl_FragColor = u_WaterColor;
		}
		
	}
}                                                               	

