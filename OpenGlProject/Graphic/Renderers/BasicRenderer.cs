using System;
using System.Collections.Generic;
using System.Linq;
using GlmNet;
using SenryakuShuriken.Core;
using SenryakuShuriken.Core.ObjectData;
using SenryakuShuriken.Graphic.Scene;
using SharpGL;
using SharpGL.SceneGraph.Core;
using SharpGL.SceneGraph.Primitives;
using SharpGL.VertexBuffers;
using Cube = SenryakuShuriken.Core.Objects.Cube;

namespace SenryakuShuriken.Graphic.Renderers
{
    public abstract class BasicRenderer : Polygon
    {
        /// <summary>The vertices that make up the polygon.</summary>
        private List<vec3> vertices = new List<vec3>();
        protected VertexBufferArray _vBo;
        private static readonly Dictionary<Type, BasicRenderer> _renderers = new Dictionary<Type, BasicRenderer>
        {
            {typeof(Cube),new CubeRenderer() }
        };
        protected readonly List<GlObject> _objects;


        protected BasicRenderer()
        {
            Gl = GameCore.Instance.Window.Scene.Gl;
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

        #region Render
        /// <summary>
        /// Renders all visible objects
        /// </summary>
        public void RenderAll()
        {
            var list = ObjectsToRender();
            foreach (var obj in list)
            {
                if (obj.Visible)
                {
                    Gl.LoadIdentity();
                    if (obj.UseRetainedRenderMode)
                    {
                        DrawRetained(obj);
                    }
                    else
                    {
                        DrawImmediate(obj);
                    }
                }
            }
        }

        private void DrawRetained(GlObject obj)
        {
            Transformation = obj.Transformation;
            _vBo.Bind(Gl);
            PushObjectSpace(Gl);
            Material?.Push(Gl);
            Render(Gl, RenderMode.Render);
            Material?.Pop(Gl);
            PopObjectSpace(Gl);
            _vBo.Unbind(Gl);
        }

        public override void Render(OpenGL gl, RenderMode renderMode)
        {
            Gl.DrawElements(OpenGL.GL_TRIANGLES, IndicesLength, OpenGL.GL_UNSIGNED_SHORT, IntPtr.Zero);
        }

        private int IndicesLength => Faces.Sum(face => face.Count);

        protected abstract void DrawImmediate(GlObject obj);
        #endregion

        protected abstract IEnumerable<GlObject> ObjectsToRender();

        #region Properties
        protected OpenGL Gl { get; }
        public List<vec3> Vertices
        {
            get
            {
                return this.vertices;
            }
            set
            {
                this.vertices = value;
            }
        }
        public List<GlObject> Objects => _objects;
        public static IEnumerable<BasicRenderer> Renderers => _renderers.Values;
        #endregion
    }
}
