import { useState } from 'react'
import { useAuth } from '../context/AuthContext'
import { useToast } from '../components/Toast'
import api from '../api/axios'
import { User, Mail, Lock, Save } from 'lucide-react'

export default function Profile() {
  const { user, updateUser } = useAuth()
  const toast = useToast()
  const [form, setForm] = useState({ username: user?.username || '', email: user?.email || '', password: '' })
  const [loading, setLoading] = useState(false)

  const handleSubmit = async (e) => {
    e.preventDefault()
    setLoading(true)
    try {
      const payload = {}
      if (form.username !== user.username) payload.username = form.username
      if (form.email !== user.email) payload.email = form.email
      if (form.password) payload.password = form.password

      if (Object.keys(payload).length === 0) {
        toast('No changes to save', 'warning')
        setLoading(false)
        return
      }

      const res = await api.put(`/users/${user.id}`, payload)
      updateUser({ ...user, username: res.data.data.username, email: res.data.data.email })
      setForm({ ...form, password: '' })
      toast('Profile updated successfully', 'success')
    } catch (err) {
      toast(err.response?.data?.message || 'Failed to update profile', 'error')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="max-w-2xl mx-auto space-y-8">
      <div>
        <h1 className="text-2xl font-bold">Profile</h1>
        <p className="text-gray-500 dark:text-gray-400 mt-1">Manage your account settings</p>
      </div>

      {/* Avatar section */}
      <div className="glass-card p-8 flex items-center gap-6">
        <div className="w-20 h-20 rounded-2xl gradient-primary flex items-center justify-center text-white text-2xl font-bold">
          {user?.username?.charAt(0)?.toUpperCase()}
        </div>
        <div>
          <h2 className="text-xl font-semibold">{user?.username}</h2>
          <p className="text-gray-500 dark:text-gray-400">{user?.email}</p>
          <span className="inline-block mt-2 px-3 py-1 rounded-full text-xs font-medium bg-indigo-50 dark:bg-indigo-900/30 text-indigo-600 dark:text-indigo-400">
            {user?.role}
          </span>
        </div>
      </div>

      {/* Edit form */}
      <div className="glass-card p-8">
        <h3 className="text-lg font-semibold mb-6">Update Information</h3>
        <form onSubmit={handleSubmit} className="space-y-5">
          <div>
            <label className="block text-sm font-medium mb-2">Full Name</label>
            <div className="relative">
              <User className="absolute left-4 top-1/2 -translate-y-1/2 w-4 h-4 text-gray-400" />
              <input
                type="text"
                value={form.username}
                onChange={(e) => setForm({ ...form, username: e.target.value })}
                className="input-field pl-11"
              />
            </div>
          </div>
          <div>
            <label className="block text-sm font-medium mb-2">Email</label>
            <div className="relative">
              <Mail className="absolute left-4 top-1/2 -translate-y-1/2 w-4 h-4 text-gray-400" />
              <input
                type="email"
                value={form.email}
                onChange={(e) => setForm({ ...form, email: e.target.value })}
                className="input-field pl-11"
              />
            </div>
          </div>
          <div>
            <label className="block text-sm font-medium mb-2">New Password</label>
            <div className="relative">
              <Lock className="absolute left-4 top-1/2 -translate-y-1/2 w-4 h-4 text-gray-400" />
              <input
                type="password"
                value={form.password}
                onChange={(e) => setForm({ ...form, password: e.target.value })}
                className="input-field pl-11"
                placeholder="Leave blank to keep current"
              />
            </div>
          </div>
          <button type="submit" disabled={loading} className="btn-primary flex items-center gap-2">
            {loading ? (
              <div className="w-5 h-5 border-2 border-white border-t-transparent rounded-full animate-spin" />
            ) : (
              <><Save className="w-4 h-4" /> Save Changes</>
            )}
          </button>
        </form>
      </div>
    </div>
  )
}
