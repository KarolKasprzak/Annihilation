#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif


varying vec4 v_color;
varying vec2 v_texCoords;
//pixel per unit
uniform int  ppu;
uniform bool isCone;
//radius in world unit
//uniform float radius;
uniform vec3 lightColor[7];
uniform vec3 lightPosition[7];
uniform float intensityArray[7];
uniform float distanceArray[7];
uniform int  arraySize;
uniform sampler2D u_texture;
uniform sampler2D u_normals;
uniform sampler2D u_lights;
uniform vec2 resolution;
uniform vec4 ambientColor;
float intensity = 1.0;


void main() {
float radius = 2 * ppu;

// sample color & normals from our textures
vec4 color = texture2D(u_texture, v_texCoords.st);
vec4 lColor = texture2D(u_lights, v_texCoords.st);
vec3 nColor = texture2D(u_normals, v_texCoords.st).rgb;


vec3 normal = normalize(nColor * 2.0 - 1.0);
vec3 sum = vec3(0.0);

	for ( int i = 0; i < arraySize; ++i ){
	   if(distance(gl_FragCoord,lightPosition[i].xy) < radius+200) {
		vec3 currentLightColor = lightColor[i];
		//The delta position of light
		//vec3 deltaPos = vec3((lightPosition[i].xy - gl_FragCoord.xy) / resolution.xy, lightPosition[i].z);
		//Correct for aspect ratio
		
		vec3 deltaPos = vec3((lightPosition[i].xy - gl_FragCoord.xy) / resolution.xy , lightPosition[i].z);
		deltaPos.x /= radius / resolution.x;
	    deltaPos.y /= radius / resolution.y;
		
		//deltaPos.x *= resolution.x / resolution.y;
		
		float d = length(deltaPos);
		
		//normalize our vectors
		vec3 N = normalize(nColor * 2.0 - 1.0);
	    vec3 L = normalize(deltaPos);
		
		//float attenuation = intensity - d;

		float lambert = clamp(dot(normal, L), 0.0, 1.0);
		
		float attenuation = smoothstep(1,0, d);
		vec3 result =(currentLightColor.rgb * lambert) * attenuation;
	
		//need upgrade
		//float attenuation = 1.0 / (1.0 + 0 * d + 5 * d *d) ;
		//float attenuation = smoothstep(distanceArray[i],0, d);
		
		//vec3 result = (currentLightColor.rgb * lColor.a) * max(dot(N, L), 0.0);
	   
		//result *= attenuation;
		sum +=  result;

		
		}
	}


vec3 ambient = ambientColor.rgb * ambientColor.a;
vec3 intensity = ambient + sum;
vec3 finalColor = color.rgb * intensity;

gl_FragColor = v_color * vec4(finalColor,color.a);

}