using System;
using System.Collections;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using OpenGlProject.Core;
using OpenGlProject.Core.ObjectData;
using OpenGlProject.Core.Objects;
using OpenGlProject.Graphic.Scene;
using SharpGL;
using SharpGL.VertexBuffers;

namespace OpenGlProject.Graphic.Renderers
{
    public abstract class BasicRenderer
    {
        protected VertexBufferArray _vBo;
        private static readonly Dictionary<Type, BasicRenderer> _renderers = new Dictionary<Type, BasicRenderer>
        {
            {typeof(Cube),new CubeRenderer() }
        };
        protected readonly List<GlObject> _objects;
        protected int _vaoCount=3;


        protected BasicRenderer()
        {
            Gl = AppCore.Instance.Window.Gl;
            _objects = new List<GlObject>();
            _vBo = GlObjectFactory.CreateVBO(Gl);

        }

        public static BasicRenderer GetRenderer(GlObject o)
        {
            var r = _renderers[o.GetType()];
            return r;
        }

        public void AddObject(GlObject obj)
        {
            Objects.Add(obj);
        }

        public void RemoveObject(GlObject obj)
        {
            Objects.Remove(obj);
        }

        public void RenderAll()
        {
            var list = ObjectsToRender();
            foreach (var obj in list)
            {
                if (obj.Visible)
                {
                    if (obj.UseVaoEnabled)
                    {
                        DrawVAO(obj);
                    }
                    else
                    {
                        Draw(obj);
                    }
                }
            }
        }

        private void DrawVAO(GlObject obj)
        {
            _vBo.Bind(Gl);
            Gl.DrawArrays(OpenGL.GL_TRIANGLES, 0, _vaoCount);
            _vBo.Unbind(Gl);
        }

        protected abstract void Draw(GlObject o);
        public abstract IEnumerable<GlObject> ObjectsToRender();

        protected OpenGL Gl { get; }
        public List<GlObject> Objects => _objects;
        public static IEnumerable<BasicRenderer> Renderers => _renderers.Values;
    }
}
