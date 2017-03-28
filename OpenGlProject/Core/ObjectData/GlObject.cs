using System;
using System.Collections.Generic;
using System.Linq;
using OpenGlProject.Core.Misc;
using OpenGlProject.Graphic.Renderers;

namespace OpenGlProject.Core.ObjectData
{
    public delegate void KeyEventHandler(GlObject sender, GlKeyEventArgs e);

    public abstract class GlObject
    {
        private static readonly Dictionary<Type, Dictionary<int, GlObject>> _objects = new Dictionary<Type, Dictionary<int, GlObject>>();
        private static int _nextId;
        private BasicRenderer _renderer;
        protected bool _visible;
        private readonly Position _position;
        private readonly Rotation _rotation;

        protected GlObject()
        {
            _renderer = BasicRenderer.GetRenderer(this);
            _renderer.AddObject(this);
            _position = new Position();
            _rotation = new Rotation();
            _visible = true;
            if (!_objects.ContainsKey(GetType()))
            {
                _objects.Add(GetType(), new Dictionary<int, GlObject>());
            }
            _objects[GetType()].Add(_nextId++, this);
            KeyboardHandler.Instance.KeyUp += (sender, args) =>
            {
                OnKeyUp(new GlKeyEventArgs(args.Key));
            };
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
        public Rotation Rotation => _rotation;
        public Position Position => _position;
        public BasicRenderer Renderer => _renderer;
        public bool Visible
        {
            get => _visible;
            set => _visible = value;
        }
    }
}
