#version 330

// zmienna wejsciowa inPosition zbierana z VAO jako atrybut 0
layout (location = 0) in vec3 inPosition;
// zmienna wejsciowa inColor zbierania z VAO jako atrybut 1
layout (location = 1) in vec3 inColor;

// zmienna wyjsciowa do shadera fragmentow
out vec3 kolorek;

uniform float dX; uniform float dY; 

void main() {
	// przypisz pozycje do zmiennej wbudowanej OpenGL
	gl_Position = vec4(inPosition.x+dX,inPosition.y+dY,inPosition.z, 1.0);
	// przepisz kolor na wyjscie z shadera
	kolorek = inColor;
}