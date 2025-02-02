#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

#define STEP_A 0.2

varying vec4 v_color;
varying vec2 v_texCoords;

uniform vec3 lightColor[7];
uniform vec3 lightPosition[7];
uniform float intensityArray[7];
uniform float distanceArray[7];
uniform int  arraySize;
uniform sampler2D u_texture;
uniform sampler2D u_normals;
uniform vec2 resolution;
uniform bool useNormals;
uniform bool useShadow;
uniform float strength;
uniform bool xInvert;
uniform vec4 ambientColor;

void main() {

// sample color & normals from our textures
vec4 color = texture2D(u_texture, v_texCoords.st);
vec3 nColor = texture2D(u_normals, v_texCoords.st).rgb;

// invert x 
nColor.r = xInvert ? 1.0 - nColor.r : nColor.r;

vec3 normal = normalize(nColor * 2.0 - 1.0);
vec3 sum = vec3(0.0);
for ( int i = 0; i < arraySize; ++i ){

	vec3 currentLightColor = lightColor[i];
	vec3 deltaPos = vec3((lightPosition[i].xy - gl_FragCoord.xy) / resolution.xy, lightPosition[i].z );
    deltaPos.x *= resolution.x / resolution.y;
	vec3 lightDir = normalize(deltaPos);
	float d = length(deltaPos);
	
	//need upgrade
	//float attenuation = 1.0 / (1.0 + 0 * d + 5 * d *d) ;
	float attenuation = smoothstep(distanceArray[i],0, d);

	vec3 result = (currentLightColor.rgb * intensityArray[i])  * clamp(dot(normal, lightDir),0.0,1.0);
	
	if (attenuation < STEP_A) 
		attenuation = 0.0;

	result *= attenuation;
	sum +=  result;

}


vec3 ambient = ambientColor.rgb * ambientColor.a;
vec3 intensity = ambient + sum;
//vec3 intensity = min(vec3(1.0), ambient + sum); // don't remember if min is critical, but I think it might be to avoid shifting the hue when multiple lights add up to something very bright.
vec3 finalColor = color.rgb * intensity;
//vec3 finalColor = sum;
gl_FragColor = v_color * vec4(finalColor,color.a);
}