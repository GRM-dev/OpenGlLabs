using System;
using System.Collections.Generic;
using System.Linq;
using System.Windows.Input;
using OpenGlProject.Core.Misc;
using OpenGlProject.Graphic.Renderers;

namespace OpenGlProject.Core.Object
{
    public delegate void KeyDownEventHandler(object sender, GlKeyEventArgs e);
    public delegate void KeyUpEventHandler(object sender, GlKeyEventArgs e);

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
            AppCore.Instance.Window.KeyUp += (sender, args) =>
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

        public void InvokeEvents()
        {
            if (KeyDown == null)
            {
                return;
            }
            var downKeys = KeyboardHandler.Instance.DownKeys.Keys;
            foreach (var k in downKeys)
            {
                KeyDown.Invoke(this, new GlKeyEventArgs(k));
            }
        }

        public event KeyDownEventHandler KeyDown;
        public event KeyUpEventHandler KeyUp;

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
