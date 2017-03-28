using System.ComponentModel;
using System.Runtime.CompilerServices;
using OpenGlProject.Properties;

namespace OpenGlProject.Graphic.ViewModel
{
    public sealed class MainWindowContext : INotifyPropertyChanged
    {
        private string _debug;
        private long _tps;

        public MainWindowContext()
        {
            PropertyChanged += OnPropertyChanged;
        }

        private void OnPropertyChanged(object sender, PropertyChangedEventArgs propertyChangedEventArgs)
        {
            Debug = "TPS: " + Tps;
        }

        public string Debug
        {
            get => _debug;
            set { _debug = value; }
        }

        public long Tps
        {
            get => _tps;
            set
            {
                _tps = value;
                OnPropertyChanged(nameof(Tps));
            }
        }

        public event PropertyChangedEventHandler PropertyChanged;

        [NotifyPropertyChangedInvocator]
        private void OnPropertyChanged([CallerMemberName] string propertyName = null)
        {
            PropertyChanged?.Invoke(this, new PropertyChangedEventArgs(propertyName));
        }
    }
}