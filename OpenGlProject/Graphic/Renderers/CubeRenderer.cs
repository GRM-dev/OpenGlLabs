using System;
using System.Collections.Generic;
using SenryakuShuriken.Core.ObjectData;
using SenryakuShuriken.Graphic.Scene;
using SharpGL;
using SharpGL.SceneGraph;
using SharpGL.SceneGraph.Primitives;
using Cube = SenryakuShuriken.Core.Objects.Cube;

namespace SenryakuShuriken.Graphic.Renderers
{
    internal class CubeRenderer : BasicRenderer
    {

        protected internal CubeRenderer() : base()
        {
            var p=new Polygon();

            p.UVs.Add(new UV(0, 0));
            p.UVs.Add(new UV(0, 1));
            p.UVs.Add(new UV(1, 1));
            p.UVs.Add(new UV(1, 0));

            //	Add the vertices.
            p.Vertices.Add(new Vertex(-1, -1, -1));
            p.Vertices.Add(new Vertex(1, -1, -1));
            p.Vertices.Add(new Vertex(1, -1, 1));
            p.Vertices.Add(new Vertex(-1, -1, 1));
            p.Vertices.Add(new Vertex(-1, 1, -1));
            p.Vertices.Add(new Vertex(1, 1, -1));
            p.Vertices.Add(new Vertex(1, 1, 1));
            p.Vertices.Add(new Vertex(-1, 1, 1));

            //	Add the faces.
            Face face = new Face(); //	bottom
            face.Indices.Add(new Index(1, 0));
            face.Indices.Add(new Index(2, 1));
            face.Indices.Add(new Index(3, 2));
            face.Indices.Add(new Index(0, 3));
            p.Faces.Add(face);

            face = new Face();      //	top
            face.Indices.Add(new Index(7, 0));
            face.Indices.Add(new Index(6, 1));
            face.Indices.Add(new Index(5, 2));
            face.Indices.Add(new Index(4, 3));
            p.Faces.Add(face);

            face = new Face();      //	right
            face.Indices.Add(new Index(5, 0));
            face.Indices.Add(new Index(6, 1));
            face.Indices.Add(new Index(2, 2));
            face.Indices.Add(new Index(1, 3));
            p.Faces.Add(face);

            face = new Face();      //	left
            face.Indices.Add(new Index(7, 0));
            face.Indices.Add(new Index(4, 1));
            face.Indices.Add(new Index(0, 2));
            face.Indices.Add(new Index(3, 3));
            p.Faces.Add(face);

            face = new Face();      // front
            face.Indices.Add(new Index(4, 0));
            face.Indices.Add(new Index(5, 1));
            face.Indices.Add(new Index(1, 2));
            face.Indices.Add(new Index(0, 3));
            p.Faces.Add(face);

            face = new Face();      //	back
            face.Indices.Add(new Index(6, 0));
            face.Indices.Add(new Index(7, 1));
            face.Indices.Add(new Index(3, 2));
            face.Indices.Add(new Index(2, 3));
            p.Faces.Add(face);
            
            _vaoCount = 6;
        }

        protected override void Draw(GlObject o)
        {
            var c = o as Cube;
            if (c == null)
            {
                throw new InvalidCastException("GlObject is null or not a Cube!");
            }

            o.Transformation.Transform(Gl);

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