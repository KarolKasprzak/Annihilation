#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif
//attributes from vertex shader
varying vec4 vColor;
varying vec2 vTexCoord;
//our texture samplers
uniform sampler2D u_texture;   //diffuse map 
uniform sampler2D u_normals;   //normal map

//values used for shading algorithm 
uniform vec2 resolution;         //resolution of screen 
uniform vec3 light[7];         //light position, normalized
uniform vec4 lightColor[7];    //light RGBA -- alpha is intensity
uniform vec4 AmbientColor;  //ambient RGBA -- alpha is intensity 
uniform vec3 attenuation;            //attenuation coefficients
 
void main() {
//RGBA of our diffuse color
vec4 DiffuseColor = texture2D(u_texture, vTexCoord);

//RGB of our normal map
vec3 NormalMap = texture2D(u_normals, vTexCoord).rgb;

vec3 sum = vec3(0.0);
for ( int i = 0; i < 7; ++i ){
//The delta position of light
//vec3 LightDir = vec3( (light.xy - gl_FragCoord.xy) / resolution.xy, light.z );
vec3 currentLight = light[i];

vec3 LightDir = vec3(currentLight.xy - (gl_FragCoord.xy / resolution.xy), currentLight.z);


//Correct for aspect ratio
LightDir.x *= resolution.x / resolution.y; 

//Determine distance (used for attenuation) BEFORE we normalize our LightDir
float D = length(LightDir);

//normalize our vectors
vec3 N = normalize(NormalMap * 2.0 - 1.0);
vec3 L = normalize(LightDir);

//Pre-multiply light color with intensity
//Then perform \"N dot L\" to determine our diffuse term
vec3 Diffuse = (lightColor[i].rgb * 1) * max(dot(N, L), 0.0);

//pre-multiply ambient color with intensity
vec3 Ambient = AmbientColor.rgb * AmbientColor.a;
			
//calculate attenuation
float Attenuation = 1.0 / ( attenuation.x + (attenuation.y*D) + (attenuation.z*D*D) );

//the calculation which brings it all together 
vec3 Intensity = Ambient + Diffuse * Attenuation;
vec3 FinalColor = DiffuseColor.rgb * Intensity;
sum += FinalColor;
}
gl_FragColor = vColor * vec4(sum, DiffuseColor.a);
}