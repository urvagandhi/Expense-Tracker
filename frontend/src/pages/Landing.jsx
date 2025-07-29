import { Link } from 'react-router-dom'
import { ArrowRight, Shield, BarChart3, Wallet, TrendingUp, Moon, Sun } from 'lucide-react'

const features = [
  {
    icon: Shield,
    title: 'Secure Authentication',
    desc: 'JWT-based auth with BCrypt password hashing keeps your financial data safe.',
  },
  {
    icon: BarChart3,
    title: 'Visual Analytics',
    desc: 'Interactive charts break down your spending by category and time period.',
  },
  {
    icon: Wallet,
    title: 'Smart Budgets',
    desc: 'Set monthly budgets with real-time tracking that auto-syncs with expenses.',
  },
]

export default function Landing({ darkMode, setDarkMode }) {
  return (
    <div className="min-h-screen">
      {/* Nav */}
      <nav className="fixed top-0 left-0 right-0 z-50 glass border-b border-white/10">
        <div className="max-w-7xl mx-auto px-6 py-4 flex items-center justify-between">
          <div className="flex items-center gap-3">
            <div className="w-9 h-9 rounded-xl gradient-primary flex items-center justify-center">
              <TrendingUp className="w-5 h-5 text-white" />
            </div>
            <span className="text-lg font-bold bg-gradient-to-r from-indigo-600 to-purple-600 bg-clip-text text-transparent">
              ExpenseTracker
            </span>
          </div>
          <div className="flex items-center gap-3">
            <button onClick={() => setDarkMode(!darkMode)} className="p-2 rounded-xl hover:bg-gray-100 dark:hover:bg-gray-800 transition-colors">
              {darkMode ? <Sun className="w-5 h-5" /> : <Moon className="w-5 h-5" />}
            </button>
            <Link to="/login" className="btn-secondary text-sm">Sign In</Link>
            <Link to="/register" className="btn-primary text-sm">Get Started</Link>
          </div>
        </div>
      </nav>

      {/* Hero */}
      <section className="relative pt-32 pb-20 overflow-hidden">
        <div className="absolute inset-0 gradient-primary opacity-5" />
        <div className="absolute top-20 right-0 w-[600px] h-[600px] bg-gradient-to-br from-indigo-400/20 to-purple-400/20 rounded-full blur-3xl" />
        <div className="absolute bottom-0 left-0 w-[400px] h-[400px] bg-gradient-to-tr from-pink-400/20 to-indigo-400/20 rounded-full blur-3xl" />

        <div className="relative max-w-7xl mx-auto px-6 grid lg:grid-cols-2 gap-16 items-center">
          <div>
            <div className="inline-flex items-center gap-2 px-4 py-1.5 rounded-full bg-indigo-50 dark:bg-indigo-900/30 text-indigo-600 dark:text-indigo-400 text-sm font-medium mb-6">
              <TrendingUp className="w-4 h-4" />
              Smart Financial Tracking
            </div>
            <h1 className="text-5xl lg:text-6xl font-bold leading-tight mb-6">
              Take Control of{' '}
              <span className="bg-gradient-to-r from-indigo-600 via-purple-600 to-pink-500 bg-clip-text text-transparent">
                Your Finances
              </span>
            </h1>
            <p className="text-lg text-gray-600 dark:text-gray-400 mb-8 max-w-lg">
              Track expenses, manage budgets, and gain insights into your spending habits.
              Built with enterprise-grade security and a clean, modern interface.
            </p>
            <div className="flex gap-4">
              <Link to="/register" className="btn-primary inline-flex items-center gap-2">
                Start Tracking <ArrowRight className="w-4 h-4" />
              </Link>
              <Link to="/login" className="btn-secondary">
                Sign In
              </Link>
            </div>
          </div>

          <div className="hidden lg:block">
            <div className="relative">
              <div className="absolute -inset-4 gradient-primary rounded-3xl opacity-20 blur-2xl" />
              <img
                src="https://images.unsplash.com/photo-1554224155-6726b3ff858f?w=800&h=600&fit=crop&q=80"
                alt="Financial analytics"
                className="relative rounded-2xl shadow-2xl w-full object-cover"
              />
            </div>
          </div>
        </div>
      </section>

      {/* Features */}
      <section className="py-20 relative">
        <div className="max-w-7xl mx-auto px-6">
          <div className="text-center mb-16">
            <h2 className="text-3xl font-bold mb-4">Everything You Need</h2>
            <p className="text-gray-600 dark:text-gray-400 max-w-2xl mx-auto">
              A complete expense management solution designed for clarity and control.
            </p>
          </div>
          <div className="grid md:grid-cols-3 gap-8">
            {features.map((f) => (
              <div key={f.title} className="glass-card p-8 hover:shadow-xl transition-shadow duration-300">
                <div className="w-12 h-12 rounded-xl gradient-primary flex items-center justify-center mb-5">
                  <f.icon className="w-6 h-6 text-white" />
                </div>
                <h3 className="text-lg font-semibold mb-3">{f.title}</h3>
                <p className="text-gray-600 dark:text-gray-400 text-sm leading-relaxed">{f.desc}</p>
              </div>
            ))}
          </div>
        </div>
      </section>

      {/* CTA */}
      <section className="py-20">
        <div className="max-w-4xl mx-auto px-6">
          <div className="gradient-primary rounded-3xl p-12 text-center text-white relative overflow-hidden">
            <div className="absolute inset-0 bg-[url('https://images.unsplash.com/photo-1579621970563-ebec7560ff3e?w=1200&h=400&fit=crop&q=60')] opacity-10 bg-cover bg-center" />
            <div className="relative">
              <h2 className="text-3xl font-bold mb-4">Ready to Get Started?</h2>
              <p className="text-white/80 mb-8 max-w-xl mx-auto">
                Join thousands of users who have taken control of their finances with our intuitive tracking platform.
              </p>
              <Link to="/register" className="inline-flex items-center gap-2 px-8 py-3 bg-white text-indigo-600 font-semibold rounded-xl hover:bg-gray-100 transition-colors shadow-lg">
                Create Free Account <ArrowRight className="w-4 h-4" />
              </Link>
            </div>
          </div>
        </div>
      </section>

      {/* Footer */}
      <footer className="border-t border-gray-200 dark:border-gray-800 py-8">
        <div className="max-w-7xl mx-auto px-6 flex items-center justify-between text-sm text-gray-500">
          <span>Expense Tracker - Built with Spring Boot + React</span>
          <span>Urva Gandhi</span>
        </div>
      </footer>
    </div>
  )
}
