using System;
using System.Windows.Input;

namespace OpenGlProject.Core.Misc
{
    public class GlKeyEventArgs : EventArgs
    {
        public GlKeyEventArgs(Key key, float delta) : this(key)
        {
            Delta = delta;
        }

        public GlKeyEventArgs(Key key)
        {
            Key = key;
        }

        public Key Key { get; }
        public bool Repeatable { get; set; } = true;
        public bool Executed { get; set; }
        public float Delta { get; }
    }
}