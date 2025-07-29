import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import { useToast } from '../components/Toast'
import { User, Mail, Lock, ArrowRight, TrendingUp } from 'lucide-react'

export default function Register() {
  const [form, setForm] = useState({ username: '', email: '', password: '', confirmPassword: '' })
  const [loading, setLoading] = useState(false)
  const [errors, setErrors] = useState({})
  const { register } = useAuth()
  const navigate = useNavigate()
  const toast = useToast()

  const update = (field, value) => setForm({ ...form, [field]: value })

  const validate = () => {
    const errs = {}
    if (!form.username || form.username.length < 2) errs.username = 'Username must be at least 2 characters'
    if (!form.email || !/\S+@\S+\.\S+/.test(form.email)) errs.email = 'Valid email is required'
    if (!form.password || form.password.length < 6) errs.password = 'Password must be at least 6 characters'
    if (form.password !== form.confirmPassword) errs.confirmPassword = 'Passwords do not match'
    setErrors(errs)
    return Object.keys(errs).length === 0
  }

  const handleSubmit = async (e) => {
    e.preventDefault()
    if (!validate()) return
    setLoading(true)
    try {
      await register(form.username, form.email, form.password)
      toast('Account created successfully!', 'success')
      navigate('/dashboard')
    } catch (err) {
      const msg = err.response?.data?.message || 'Registration failed'
      toast(msg, 'error')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="min-h-screen flex">
      <div className="hidden lg:flex lg:w-1/2 relative">
        <div className="absolute inset-0 gradient-primary opacity-90" />
        <img
          src="https://images.unsplash.com/photo-1553729459-afe8f2e2ed65?w=1200&h=900&fit=crop&q=80"
          alt="Finance"
          className="absolute inset-0 w-full h-full object-cover mix-blend-overlay"
        />
        <div className="relative z-10 flex flex-col justify-center p-16 text-white">
          <div className="flex items-center gap-3 mb-8">
            <div className="w-10 h-10 rounded-xl bg-white/20 flex items-center justify-center">
              <TrendingUp className="w-5 h-5" />
            </div>
            <span className="text-xl font-bold">ExpenseTracker</span>
          </div>
          <h2 className="text-4xl font-bold mb-4">Get Started</h2>
          <p className="text-white/70 text-lg max-w-md">
            Create your account and start managing your expenses with powerful tracking and analytics tools.
          </p>
        </div>
      </div>

      <div className="flex-1 flex items-center justify-center p-8">
        <div className="w-full max-w-md">
          <div className="lg:hidden flex items-center gap-3 mb-8">
            <div className="w-9 h-9 rounded-xl gradient-primary flex items-center justify-center">
              <TrendingUp className="w-5 h-5 text-white" />
            </div>
            <span className="text-lg font-bold bg-gradient-to-r from-indigo-600 to-purple-600 bg-clip-text text-transparent">ExpenseTracker</span>
          </div>

          <h1 className="text-2xl font-bold mb-2">Create Account</h1>
          <p className="text-gray-500 dark:text-gray-400 mb-8">
            Already have an account?{' '}
            <Link to="/login" className="text-indigo-600 dark:text-indigo-400 font-medium hover:underline">Sign in</Link>
          </p>

          <form onSubmit={handleSubmit} className="space-y-5">
            {[
              { field: 'username', label: 'Full Name', icon: User, type: 'text', placeholder: 'John Doe' },
              { field: 'email', label: 'Email', icon: Mail, type: 'email', placeholder: 'you@example.com' },
              { field: 'password', label: 'Password', icon: Lock, type: 'password', placeholder: 'Min 6 characters' },
              { field: 'confirmPassword', label: 'Confirm Password', icon: Lock, type: 'password', placeholder: 'Repeat password' },
            ].map(({ field, label, icon: Icon, type, placeholder }) => (
              <div key={field}>
                <label className="block text-sm font-medium mb-2">{label}</label>
                <div className="relative">
                  <Icon className="absolute left-4 top-1/2 -translate-y-1/2 w-4 h-4 text-gray-400" />
                  <input
                    type={type}
                    value={form[field]}
                    onChange={(e) => update(field, e.target.value)}
                    className="input-field pl-11"
                    placeholder={placeholder}
                  />
                </div>
                {errors[field] && <p className="text-red-500 text-xs mt-1">{errors[field]}</p>}
              </div>
            ))}

            <button type="submit" disabled={loading} className="w-full btn-primary flex items-center justify-center gap-2 py-3">
              {loading ? (
                <div className="w-5 h-5 border-2 border-white border-t-transparent rounded-full animate-spin" />
              ) : (
                <>Create Account <ArrowRight className="w-4 h-4" /></>
              )}
            </button>
          </form>
        </div>
      </div>
    </div>
  )
}
