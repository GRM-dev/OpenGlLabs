using System;
using System.Collections.Generic;
using System.Linq;
using OpenGlProject.Core.Misc;
using OpenGlProject.Graphic.Renderers;
using OpenGlProject.Graphic.Scene;
using SharpGL.SceneGraph.Transformations;
using SharpGL.VertexBuffers;

namespace OpenGlProject.Core.ObjectData
{
    public delegate void KeyEventHandler(GlObject sender, GlKeyEventArgs e);

    public abstract class GlObject
    {
        private static readonly Dictionary<Type, Dictionary<int, GlObject>> _objects = new Dictionary<Type, Dictionary<int, GlObject>>();
        private static int _nextId;
        private readonly BasicRenderer _renderer;
        private readonly LinearTransformation _transformation;
        protected bool _visible;
        protected bool _useVAO = false;

        protected GlObject()
        {
            _renderer = BasicRenderer.GetRenderer(this);
            _renderer.AddObject(this);
            _transformation = new LinearTransformation();
            _visible = true;
            KeyboardHandler.Instance.KeyUp += args =>
            {
                OnKeyUp(new GlKeyEventArgs(args.Key));
            };
            if (!_objects.ContainsKey(GetType()))
            {
                _objects.Add(GetType(), new Dictionary<int, GlObject>());
            }
            _objects[GetType()].Add(_nextId++, this);
        }

        protected virtual void OnKeyDown(GlKeyEventArgs e)
        {
            KeyDown?.Invoke(this, e);
        }

        protected virtual void OnKeyUp(GlKeyEventArgs e)
        {
            KeyUp?.Invoke(this, e);
        }

        public void InvokeEvents(StopwatchEventArgs args)
        {
            OnTick?.Invoke(this, args);
            if (KeyDown != null)
            {
                var downKeys = KeyboardHandler.Instance.DownKeys.Keys;
                foreach (var k in downKeys)
                {
                    KeyDown.Invoke(this, new GlKeyEventArgs(k, args.Delta));
                }
            }
        }

        public event KeyEventHandler KeyDown;
        public event KeyEventHandler KeyUp;
        public event TickEventHandler OnTick;

        public static List<GlObject> Objects => (from d in _objects.Values from o in d select o.Value).ToList();
        public LinearTransformation Transformation => _transformation;
        public BasicRenderer Renderer => _renderer;
        public bool Visible
        {
            get => _visible;
            set => _visible = value;
        }
        public bool UseVaoEnabled => _useVAO;
    }
}
