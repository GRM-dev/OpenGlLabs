using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using OpenGlProject.Core;
using OpenGlProject.Core.ObjectData;
using SharpGL;
using SharpGL.VertexBuffers;

namespace OpenGlProject.Graphic.Scene
{
    public static class GlObjectFactory
    {
        public static VertexBufferArray CreateVBO(OpenGL gl)
        {
            var vbo = new VertexBufferArray();
            vbo.Create(gl);
            return vbo;
        }

        public static VertexBuffer CreateVertexBuffer(OpenGL gl, uint attributeIndex, float[] vertices, int stride, bool isNormalised=false)
        {
            var vertexBuffer = new VertexBuffer();
            vertexBuffer.Create(gl);
            vertexBuffer.Bind(gl);
            vertexBuffer.SetData(gl, attributeIndex, vertices, isNormalised, stride);
            return vertexBuffer;
        }
    }
}
