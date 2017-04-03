#version 330

uniform vec3 LightColor;
uniform vec3 LightDirection;
uniform float LightAmbient;
uniform sampler2D gSampler;

in vec4 kolorek;
in vec3 vNormal;
in vec2 texCoord;
in float fogCoord;

out vec4 outputColor;

uniform float FogDensity;

vec3 FogColor = vec3(0.6, 0.6, 0.6);
vec4 FragmentColor;

void main()
{
	float FogFactor = 1.0 - clamp(exp(-pow(FogDensity*fogCoord, 2)), 0.0, 1.0);
	float LightDiffuse = max(0.0, dot(normalize(vNormal), -LightDirection));
	FragmentColor = kolorek*texture2D(gSampler, texCoord)*vec4(LightColor*(LightAmbient+LightDiffuse),1.0);
	outputColor = mix(FragmentColor, vec4(FogColor, 1.0), FogFactor);
}