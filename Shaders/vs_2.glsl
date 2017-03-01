// definicja wersji
#version 330

// zmienna wejsciowa inPosition zbierana z VAO jako atrybut 0
layout (location = 0) in vec3 inPosition;

void main()
{	
	// przypisz pozycje do zmiennej wbudowanej OpenGL
	gl_Position = vec4(inPosition, 1.0); 

}