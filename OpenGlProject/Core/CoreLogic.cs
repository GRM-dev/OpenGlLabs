using System;
using System.Diagnostics;
using System.Threading;
using System.Timers;
using Microsoft.Win32;
using OpenGlProject.Core.Object;
using OpenGlProject.Graphic.ViewModel;

namespace OpenGlProject.Core
{
    public delegate void TickEventHandler(CoreLogic logic, StopwatchEventArgs args);

    public class CoreLogic
    {
        public static readonly int TPS = 20;
        public readonly TimeSpan TICK_TIME = TimeSpan.FromMilliseconds(1000 / TPS);
        private readonly Thread _thread;
        private bool _stop;
        private Stopwatch _stopwatch;
        private long _lastTickTime;
        private int _currentTick;
        private long _lastTpsCount;
        private long _cycleCounter;
        private TimeSpan _lastTickInvokeTime;

        public CoreLogic(MainAppContext mainAppContext)
        {
            _thread = new Thread(Run);
            AppContext = mainAppContext;
        }

        private void Run()
        {
            while (!_stop)
            {
                if (_stopwatch.Elapsed - _lastTickInvokeTime > TICK_TIME)
                {
                    KeyboardHandler.Instance.InvokeGlobalEvents();
                    foreach (var o in GlObject.Objects)
                    {
                        o.InvokeEvents();
                    }
                    _currentTick++;
                    _lastTickInvokeTime = _stopwatch.Elapsed;
                    EachTick?.Invoke(this, new StopwatchEventArgs(_cycleCounter, _stopwatch.ElapsedMilliseconds, _currentTick));
                }
                if (_stopwatch.Elapsed > TimeSpan.FromMilliseconds(1000))
                {
                    EachCycle?.Invoke(this, new StopwatchEventArgs(_cycleCounter, _stopwatch.ElapsedMilliseconds, _currentTick));
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
                GetDelta();
                _thread.Start();
                EachCycle += (logic, args) => AppContext.Tps = LastTpsCount;
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

        public long GetDelta()
        {
            var currentTime = GetCurrentCycleTime();
            var delta = currentTime - _lastTickTime;
            _lastTickTime = currentTime;
            return delta;
        }

        public long GetCurrentCycleTime()
        {
            return _stopwatch.ElapsedTicks;
        }

        public event TickEventHandler EachTick;
        public event TickEventHandler EachCycle;

        public int CurrentTick => _currentTick;
        public long LastTpsCount => _lastTpsCount;
        public MainAppContext AppContext { get; }
    }
}
