using System;
using System.Diagnostics;
using System.Threading;
using System.Timers;
using Microsoft.Win32;
using OpenGlProject.Core.ObjectData;
using OpenGlProject.Graphic.ViewModel;

namespace OpenGlProject.Core
{
    public delegate void TickEventHandler(object sender, StopwatchEventArgs args);

    public class CoreLogic
    {
        public static readonly int TPS = 80;
        public readonly TimeSpan TICK_TIME = TimeSpan.FromMilliseconds(1000 / TPS);
        private readonly Thread _thread;
        private bool _stop;
        private Stopwatch _stopwatch;
        private int _currentTick;
        private long _lastTpsCount;
        private long _cycleCounter;
        private TimeSpan _lastTickInvokeTime;

        public CoreLogic(MainWindowContext mainWindowContext)
        {
            _thread = new Thread(Run);
            WindowContext = mainWindowContext;
        }

        private void Run()
        {
            while (!_stop)
            {
                var elapsedTimeFromLastTick = _stopwatch.Elapsed;
                var delta = elapsedTimeFromLastTick - _lastTickInvokeTime;
                var d = delta.Milliseconds / TICK_TIME.Milliseconds;
                if (delta > TICK_TIME)
                {
                    var args = new StopwatchEventArgs(_cycleCounter, _stopwatch.ElapsedMilliseconds, _currentTick, d);
                    KeyboardHandler.Instance.InvokeGlobalEvents(d);
                    foreach (var o in GlObject.Objects)
                    {
                        o.InvokeEvents(args);
                    }
                    _currentTick++;
                    _lastTickInvokeTime = elapsedTimeFromLastTick;
                    EachTick?.Invoke(this, args);
                }
                if (elapsedTimeFromLastTick > TimeSpan.FromMilliseconds(1000))
                {
                    var stopwatchEventArgs = new StopwatchEventArgs(_cycleCounter, _stopwatch.ElapsedMilliseconds, _currentTick, d);
                    EachCycle?.Invoke(this, stopwatchEventArgs);
                    _lastTpsCount = _currentTick;
                    _lastTickInvokeTime = TimeSpan.Zero;
                    _currentTick = 0;
                    _cycleCounter++;
                    _stopwatch.Restart();
                }
            }
        }

        public void Start()
        {
            if (!_thread.IsAlive)
            {
                _stopwatch = new Stopwatch();
                _stopwatch.Start();
                _thread.Start();
                EachCycle += (logic, args) => WindowContext.Tps = LastTpsCount;
            }
        }

        public void Stop()
        {
            _stop = true;
            if (_thread.IsAlive)
            {
                Thread.Sleep(100);
                _thread.Abort();
            }
        }

        public long GetCurrentCycleTime()
        {
            return _stopwatch.ElapsedTicks;
        }

        public event TickEventHandler EachTick;
        public event TickEventHandler EachCycle;

        public int CurrentTick => _currentTick;
        public long LastTpsCount => _lastTpsCount;
        public MainWindowContext WindowContext { get; }
    }
}
