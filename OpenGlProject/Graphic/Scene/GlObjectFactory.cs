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
            vbo.Bind(gl);
            return vbo;
        }

    }
}
