#version 330

uniform mat4 projectionMatrix;
uniform mat4 modelViewMatrix;
uniform mat4 normalMatrix;

layout (location = 0) in vec3 inPosition;
layout (location = 1) in vec3 inColor;
layout (location = 2) in vec3 inNormal;
layout (location = 3) in vec3 inTexCoord;

out vec3 vNormal;
smooth out vec4 kolorek;

out vec2 texCoord;

void main()
{	
	gl_Position = projectionMatrix*modelViewMatrix*vec4(inPosition, 1.0);
	vec4 vRes = normalMatrix*vec4(inNormal, 0.0);
	vNormal = vRes.xyz;
	kolorek = vec4(inColor,1.0);	
	texCoord = vec2(inTexCoord[0],inTexCoord[1]);	
}