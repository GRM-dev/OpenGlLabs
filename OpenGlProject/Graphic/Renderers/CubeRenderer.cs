using System;
using System.Collections.Generic;
using OpenGlProject.Core;
using OpenGlProject.Core.ObjectData;
using OpenGlProject.Core.Objects;
using SharpGL;

namespace OpenGlProject.Graphic.Renderers
{
    internal class CubeRenderer : BasicRenderer
    {

        protected internal CubeRenderer()
        {
        }

        protected override void Draw(GlObject o)
        {
            var c = o as Cube;
            if (c == null)
            {
                throw new InvalidCastException("GlObject is null or not a Cube!");
            }

            Gl.Translate(c.Position.X, c.Position.Y, c.Position.Z);
            Gl.Rotate(c.Rotation.Rx, 1.0f, 0.0f, 0.0f);
            Gl.Rotate(c.Rotation.Ry, 0.0f, 1.0f, 0.0f);
            Gl.Rotate(c.Rotation.Rz, 0.0f, 0.0f, 1.0f);

            Gl.Begin(OpenGL.GL_QUADS);

            Gl.Color(0.0f, 1.0f, 0.0f);
            Gl.Vertex(1.0f, 1.0f, -1.0f);
            Gl.Vertex(-1.0f, 1.0f, -1.0f);
            Gl.Vertex(-1.0f, 1.0f, 1.0f);
            Gl.Vertex(1.0f, 1.0f, 1.0f);

            Gl.Color(1.0f, 0.5f, 0.0f);
            Gl.Vertex(1.0f, -1.0f, 1.0f);
            Gl.Vertex(-1.0f, -1.0f, 1.0f);
            Gl.Vertex(-1.0f, -1.0f, -1.0f);
            Gl.Vertex(1.0f, -1.0f, -1.0f);

            Gl.Color(1.0f, 0.0f, 0.0f);
            Gl.Vertex(1.0f, 1.0f, 1.0f);
            Gl.Vertex(-1.0f, 1.0f, 1.0f);
            Gl.Vertex(-1.0f, -1.0f, 1.0f);
            Gl.Vertex(1.0f, -1.0f, 1.0f);

            Gl.Color(1.0f, 1.0f, 0.0f);
            Gl.Vertex(1.0f, -1.0f, -1.0f);
            Gl.Vertex(-1.0f, -1.0f, -1.0f);
            Gl.Vertex(-1.0f, 1.0f, -1.0f);
            Gl.Vertex(1.0f, 1.0f, -1.0f);

            Gl.Color(0.0f, 0.0f, 1.0f);
            Gl.Vertex(-1.0f, 1.0f, 1.0f);
            Gl.Vertex(-1.0f, 1.0f, -1.0f);
            Gl.Vertex(-1.0f, -1.0f, -1.0f);
            Gl.Vertex(-1.0f, -1.0f, 1.0f);

            Gl.Color(1.0f, 0.0f, 1.0f);
            Gl.Vertex(1.0f, 1.0f, -1.0f);
            Gl.Vertex(1.0f, 1.0f, 1.0f);
            Gl.Vertex(1.0f, -1.0f, 1.0f);
            Gl.Vertex(1.0f, -1.0f, -1.0f);

            Gl.End();
        }

        public override IEnumerable<GlObject> ObjectsToRender()
        {
            return Cubes;
        }

        private IEnumerable<Cube> Cubes => _objects.ConvertAll(o => o as Cube);
    }
}