#version 330

uniform vec3 LightColor;
uniform vec3 LightDirection;
uniform int ShadingMode;
uniform sampler2D gSampler;

uniform float LightAmbient;

smooth in vec4 kolorek;
in vec3 vNormal;
in vec2 texCoord;

out vec4 outputColor;
vec4 vTexColor;

void main()
{
	  vTexColor = texture2D(gSampler, texCoord);	
	  float LightDiffuse = max(0.0, dot(normalize(vNormal), -LightDirection));	  
	  outputColor = kolorek*vTexColor*vec4(LightColor*(LightAmbient+LightDiffuse),1.0);	
}