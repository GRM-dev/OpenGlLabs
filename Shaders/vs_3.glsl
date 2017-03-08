// definicja wersji
#version 330

uniform mat4 projectionMatrix;
uniform mat4 modelViewMatrix;

// zmienna wejsciowa inPosition zbierana z VAO jako atrybut 0
layout (location = 0) in vec3 inPosition;

// zmienna wejsciowa inColor zbierania z VAO jako atrybut 1
layout (location = 1) in vec3 inColor;

// zmienna wyjsciowa do shadera fragmentow 
out vec3 kolorek;

void main()
{	
	// przypisz pozycje do zmiennej wbudowanej OpenGL	
	gl_Position = vec4(inPosition, 1.0);
	// przepisz kolor na wyjscie z shadera
	kolorek = inColor; 
}