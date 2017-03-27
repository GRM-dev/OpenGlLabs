#version 330

uniform vec3 LightColor;
uniform vec3 LightDirection;
uniform float LightAmbient;

in vec4 kolorek;
in vec3 vNormal;

out vec4 outputColor;


void main()
{
	float LightDiffuse = max(0.0, dot(normalize(vNormal), -LightDirection));
	outputColor = kolorek*vec4(LightColor*(LightAmbient+LightDiffuse),1.0);		
}