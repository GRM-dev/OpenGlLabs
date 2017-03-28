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
            var cube = o as Cube;
            if (cube == null)
            {
                throw new InvalidCastException("GlObject is null or not a Cube!");
            }
            Gl.LoadIdentity();

            Gl.Translate(1.5f, 0.0f, -7.0f);
            Gl.Rotate(cube.Rotation.Rx, 1.0f, 0.0f, 0.0f);
            Gl.Rotate(cube.Rotation.Ry, 0.0f, 1.0f, 0.0f);
            Gl.Rotate(cube.Rotation.Rz, 0.0f, 0.0f, 1.0f);

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