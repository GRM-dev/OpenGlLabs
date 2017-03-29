#version 330

uniform vec3 LightColor;
uniform vec3 LightDirection;
uniform float LightAmbient;
uniform sampler2D gSampler;

in vec4 kolorek;
in vec3 vNormal;
in vec2 texCoord;


out vec4 outputColor;

void main()
{		
	float LightDiffuse = max(0.0, dot(normalize(vNormal), -LightDirection));		
	outputColor = kolorek*texture2D(gSampler, texCoord)*vec4(LightColor*(LightAmbient+LightDiffuse),1.0);		
}