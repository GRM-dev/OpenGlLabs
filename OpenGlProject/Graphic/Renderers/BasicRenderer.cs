using System;
using System.Collections;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using OpenGlProject.Core;
using OpenGlProject.Core.Object;
using OpenGlProject.Core.Objects;
using SharpGL;

namespace OpenGlProject.Graphic.Renderers
{
    public abstract class BasicRenderer
    {
        private static Dictionary<Type, BasicRenderer> _renderers = new Dictionary<Type, BasicRenderer>
        {
            {typeof(Cube),new CubeRenderer() }
        };
        protected readonly List<GlObject> _objects;


        protected BasicRenderer()
        {
            Gl = AppCore.Instance.Window.Gl;
            _objects = new List<GlObject>();
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

        public abstract void RenderAll();

        protected OpenGL Gl { get; }
        public List<GlObject> Objects => _objects;
        public static IEnumerable<BasicRenderer> Renderers => _renderers.Values;
    }
}
