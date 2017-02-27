#version 330

layout (location = 0) in vec3 inPosition;

smooth out vec3 theColor;

void main()
{
	gl_Position = vec4(inPosition, 1.0);
	theColor = vec3(0.0,0.0,0.0);	
}