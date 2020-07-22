#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif
varying vec4 v_color;
varying vec2 v_texCoords;

uniform vec3 lightColor[7];
uniform vec3 lightPosition[7];

uniform sampler2D u_texture;
uniform sampler2D u_normals;
uniform vec2 resolution;
uniform bool useNormals;
uniform bool useShadow;
uniform float strength;
uniform bool yInvert;
uniform bool xInvert;
uniform vec4 ambientColor;

void main() {

// sample color & normals from our textures
vec4 color = texture2D(u_texture, v_texCoords.st);
vec3 nColor = texture2D(u_normals, v_texCoords.st).rgb;

// some bump map programs will need the Y value flipped..
nColor.g = yInvert ? 1.0 - nColor.g : nColor.g;
nColor.r = xInvert ? 1.0 - nColor.r : nColor.r;


vec3 normal = normalize(nColor * 2.0 - 1.0);
vec3 sum = vec3(0.0);
for ( int i = 0; i < lightPosition.length(); ++i ){


	vec3 currentLightColor = lightColor[i];
	// here we do a simple distance calculation

	vec3 deltaPos = vec3( (lightPosition[i].xy - gl_FragCoord.xy) / resolution.xy, lightPosition[i].z );


	vec3 lightDir = normalize(deltaPos);

	float d = length(deltaPos);
	

	
	//need upgrade
	//float attenuation = 1.0 / (1.0 + 0 * d + 5 * d *d) ;
	float attenuation = smoothstep(0.6,0, deltaPos);

	vec3 result = (currentLightColor.rgb * 1.0) * max(dot(normal, lightDir),0.0);

	result *= attenuation;
	sum +=  result;
	
	
	
}


vec3 ambient = ambientColor.rgb * ambientColor.a;
vec3 intensity = min(vec3(1.0), ambient + sum); // don't remember if min is critical, but I think it might be to avoid shifting the hue when multiple lights add up to something very bright.
vec3 finalColor = color.rgb * intensity;
//vec3 finalColor = sum;

//finalColor *= (ambientColor.rgb * ambientColor.a);
gl_FragColor = v_color * vec4(finalColor, color.a);
}