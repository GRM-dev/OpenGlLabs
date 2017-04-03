#version 330

uniform mat4 projectionMatrix;
uniform mat4 modelViewMatrix;
uniform mat4 normalMatrix;

uniform float ColorAlpha;

layout (location = 0) in vec3 inPosition;
layout (location = 1) in vec3 inColor;
layout (location = 2) in vec3 inNormal;
layout (location = 3) in vec3 inTexCoord;

out vec3 vNormal;
out vec4 kolorek;
out vec2 texCoord;
out float fogCoord;

void main()
{
	vec4 EyeSpaceCoord = modelViewMatrix*vec4(inPosition, 1.0);
	gl_Position = projectionMatrix*EyeSpaceCoord;
	vec4 vRes = normalMatrix*vec4(inNormal, 0.0);
	vNormal = vRes.xyz;
	kolorek = vec4(inColor, ColorAlpha);
	texCoord = vec2(inTexCoord[0], inTexCoord[1]);
	fogCoord = abs(EyeSpaceCoord.z);
}