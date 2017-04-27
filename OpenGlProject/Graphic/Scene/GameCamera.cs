using System.Windows.Input;
using SenryakuShuriken.Core;
using SharpGL;
using SharpGL.SceneGraph;
using SharpGL.SceneGraph.Cameras;

namespace SenryakuShuriken.Graphic.Scene
{
    /// <summary>
    /// Handles Camera transformations
    /// </summary>
    public class GameCamera : PerspectiveCamera
    {
        #region Fields
        private double fieldOfView = 60.0;
        private double near = 0.5;
        private double far = 40.0;
        private Vertex _viewPoint;
        private Vertex _upVertex;
        #endregion

        public GameCamera()
        {
            _viewPoint = new Vertex(0.0f, 10.0f, 0.0f);
            _upVertex = new Vertex(0.0f, 0.0f, 1f);
            var pos = new Vertex(0f, -10f, 0f);
            Position = pos;
        }

        /// <summary>
        /// Assigns base, global Event Handlers
        /// </summary>
        public void AssignCameraKeys()
        {
            var kh = KeyboardHandler.Instance;
            kh.KeyPressed += args =>
            {
                var camPosition = Position;
                var dx = 1f;
                var dy = 1f;
                var dz = 1f;
                switch (args.Key)
                {
                    case Key.PageUp:
                        camPosition.Y += dy;
                        _viewPoint.Y += dy;
                        break;
                    case Key.PageDown:
                        camPosition.Y -= dy;
                        _viewPoint.Y -= dy;
                        break;
                }
                Position = camPosition;
            };
        }

        public override void TransformProjectionMatrix(OpenGL gl)
        {
            gl.Perspective(fieldOfView, AspectRatio, near, far);
            var p = Position + _viewPoint;
            gl.LookAt(Position.X, Position.Y, Position.Z, p.X, p.Y, p.Z, _upVertex.X, _upVertex.Y, _upVertex.Z);
        }
        /// <summary>
        /// When window is resized resize opengl window and perspective also
        /// </summary>
        /// <param name="gl"></param>
        public void OnResized(OpenGL gl)
        {//me has permission to grant 3 key fragments to players with best positive behavior in this game.
            // Load and clear the projection matrix.
            gl.MatrixMode(OpenGL.GL_PROJECTION);
            gl.LoadIdentity();

            // Perform a perspective transformation
            gl.Perspective(45.0f, gl.RenderContextProvider.Width /
                                  (float)gl.RenderContextProvider.Height,
                0.1f, 100.0f);
            // Load the modelview.
            gl.MatrixMode(OpenGL.GL_MODELVIEW);
        }
    }
}
