#version 330

// zmienna wejsciowa inPosition zbierana z VAO jako atrybut 0
layout (location = 0) in vec3 inPosition1;
// zmienna wejsciowa inColor zbierania z VAO jako atrybut 1
layout (location = 1) in vec3 inColor1;

layout (location = 0) in vec3 inPosition2;
layout (location = 1) in vec3 inColor2;

// zmienna wyjsciowa do shadera fragmentow
out vec3 kolorek1;
out vec3 kolorek2;

uniform float dX1; uniform float dY1; 
uniform float dX2; uniform float dY2; 

void main() {
	// przypisz pozycje do zmiennej wbudowanej OpenGL
	gl_Position = vec4(inPosition1.x+dX1,inPosition1.y+dY1,inPosition1.z, 1.0);
	// przepisz kolor na wyjscie z shadera
	kolorek1 = inColor1;

	gl_Position = vec4(inPosition2.x+dX2,inPosition2.y+dY2,inPosition2.z, 1.0);
	kolorek2 = inColor2;
}