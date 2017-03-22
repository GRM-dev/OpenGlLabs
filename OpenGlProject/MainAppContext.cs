using System.ComponentModel;
using System.Runtime.CompilerServices;
using OpenGlProject.Annotations;

namespace OpenGlProject
{
    public sealed class MainAppContext : INotifyPropertyChanged
    {
        private float _rquad;
        private float _rotatePyramid;
        private string _debug;

        public MainAppContext()
        {

        }

        public void UpdateView()
        {

        }

        public float Rquad
        {
            get => _rquad;
            set
            {
                _rquad = value;
                OnPropertyChanged(nameof(Rquad));
            }
        }

        public float RotatePyramid
        {
            get => _rotatePyramid;
            set
            {
                _rotatePyramid = value;
                OnPropertyChanged(nameof(RotatePyramid));
            }
        }

        public string Debug
        {
            get { return _debug; }
            set { _debug = value; }
        }

        public event PropertyChangedEventHandler PropertyChanged;

        [NotifyPropertyChangedInvocator]
        private void OnPropertyChanged([CallerMemberName] string propertyName = null)
        {
            PropertyChanged?.Invoke(this, new PropertyChangedEventArgs(propertyName));
        }
    }
}