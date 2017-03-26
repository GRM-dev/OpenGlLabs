using System;
using System.Windows.Input;
using OpenGlProject.Core.Object;

namespace OpenGlProject.Core.Misc
{
    public class GlKeyEventArgs : EventArgs
    {
        public GlKeyEventArgs(Key key)
        {
            Key = key;
        }

        public Key Key { get; }
        public bool Repeatable { get; set; } = true;
        public bool Executed { get; set; }
    }
}