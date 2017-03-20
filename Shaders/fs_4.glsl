#version 330

uniform vec3 LightColor; 
uniform float LightAmbient;

in vec4 kolorek;

out vec4 outputColor;

void main()
{	
	outputColor = kolorek*vec4(LightColor*(LightAmbient),1.0);
}