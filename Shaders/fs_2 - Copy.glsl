#version 330

in vec3 kolorek1; // zmienna wejsciowa 
out vec4 outputColor1; // zmienna wyjsciowa

in vec3 kolorek2;
out vec4 outputColor2;

void main() { 
	outputColor1 = vec4(kolorek1, 1.0);
	outputColor2 = vec4(kolorek2, 1.0);
}