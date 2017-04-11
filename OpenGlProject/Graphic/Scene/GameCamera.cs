using System.Windows.Input;
using OpenGlProject.Core;
using SharpGL;
using SharpGL.SceneGraph;
using SharpGL.SceneGraph.Cameras;

namespace OpenGlProject.Graphic.Scene
{
    public class GameCamera : PerspectiveCamera
    {
        private double fieldOfView = 60.0;
        private double near = 0.5;
        private double far = 40.0;
        private Vertex _viewPoint;
        private Vertex _upVertex;

        public GameCamera()
        {
            _viewPoint = new Vertex(0.0f, 0.0f, 0.0f);
            _upVertex = new Vertex(0.0f, 0.0f, 1f);
            var pos = new Vertex(0f, -20f, 0f);
            Position = pos;
        }

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
                    case Key.A:
                        camPosition.X -= dx;
                        _viewPoint.X -= dx;
                        break;
                    case Key.D:
                        camPosition.X += dx;
                        _viewPoint.X += dx;
                        break;
                    case Key.W:
                        camPosition.Y += dy;
                        _viewPoint.Y += dy;
                        break;
                    case Key.S:
                        camPosition.Y -= dy;
                        _viewPoint.Y -= dy;
                        break;
                    case Key.Z:
                        camPosition.Z += dz;
                        _viewPoint.Z += dz;
                        break;
                    case Key.X:
                        camPosition.Z -= dz;
                        _viewPoint.Z -= dz;
                        break;
                    case Key.E:
                        _viewPoint.X += dx;
                        break;
                    case Key.Q:
                        _viewPoint.X -= dx;
                        break;
                }
                Position = camPosition;
            };
        }

        public override void TransformProjectionMatrix(OpenGL gl)
        {
            gl.Perspective(fieldOfView, AspectRatio, near, far);
            var p =  _viewPoint;
            gl.LookAt(Position.X, Position.Y, Position.Z, p.X, p.Y, p.Z, _upVertex.X, _upVertex.Y, _upVertex.Z);
        }
    }
}
