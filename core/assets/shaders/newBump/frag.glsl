#version 130
#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif
varying vec4 v_color;
varying vec2 v_texCoords;

uniform vec3 lightColor[7];
uniform vec3 light[7];

uniform sampler2D u_texture;
uniform sampler2D u_normals;
uniform vec2 resolution;
uniform bool useNormals;
uniform bool useShadow;
uniform float strength;
uniform bool yInvert;
uniform bool xInvert;
uniform vec4 ambientColor;
uniform float u_intensity = 1.0;
void main()
                {
vec2 screenPos = gl_FragCoord.xy / resolution.xy;
vec3 NormalMap = texture2D(u_normals, screenPos).rgb;

float sum = 0;
for ( int i = 0; i < 7; ++i ){
vec3 currentLight = light[i];
vec3 currentLightColor = lightColor[i];

vec3 LightDir = vec3(currentLight.xy - screenPos, currentLight.z);

vec3 N = normalize(NormalMap * 2.0 - 1.0);

vec3 L = normalize(LightDir);

float maxProd = max(dot(N, L), 0.0);
sum = maxProd;
}
              
gl_FragColor = v_color * sum * u_intensity;
}
