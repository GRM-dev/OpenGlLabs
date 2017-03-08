#version 330

in vec3 kolorek; // zmienna wejsciowa 
out vec4 outputColor; // zmienna wyjsciowa 

void main()
{
	outputColor = vec4(kolorek, 1.0);
}