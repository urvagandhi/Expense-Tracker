import { useState, useEffect } from 'react'
import api from '../api/axios'
import { useToast } from '../components/Toast'
import { Plus, Pencil, Trash2, X, Wallet } from 'lucide-react'

const MONTHS = ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec']

export default function Budgets() {
  const [budgets, setBudgets] = useState([])
  const [loading, setLoading] = useState(true)
  const [showModal, setShowModal] = useState(false)
  const [editing, setEditing] = useState(null)
  const [form, setForm] = useState({ month: new Date().getMonth() + 1, year: new Date().getFullYear(), budgetLimit: '' })
  const [errors, setErrors] = useState({})
  const toast = useToast()

  const fetchBudgets = () => {
    api.get('/budgets')
      .then((res) => setBudgets(res.data.data || []))
      .finally(() => setLoading(false))
  }

  useEffect(() => { fetchBudgets() }, [])

  const openAdd = () => {
    setEditing(null)
    setForm({ month: new Date().getMonth() + 1, year: new Date().getFullYear(), budgetLimit: '' })
    setErrors({})
    setShowModal(true)
  }

  const openEdit = (budget) => {
    setEditing(budget)
    setForm({ month: budget.month, year: budget.year, budgetLimit: budget.budgetLimit.toString() })
    setErrors({})
    setShowModal(true)
  }

  const validate = () => {
    const errs = {}
    if (!form.budgetLimit || parseFloat(form.budgetLimit) <= 0) errs.budgetLimit = 'Budget limit must be positive'
    if (form.month < 1 || form.month > 12) errs.month = 'Invalid month'
    setErrors(errs)
    return Object.keys(errs).length === 0
  }

  const handleSubmit = async (e) => {
    e.preventDefault()
    if (!validate()) return
    const payload = { month: parseInt(form.month), year: parseInt(form.year), budgetLimit: parseFloat(form.budgetLimit) }
    try {
      if (editing) {
        await api.put(`/budgets/${editing.id}`, payload)
        toast('Budget updated', 'success')
      } else {
        await api.post('/budgets', payload)
        toast('Budget created', 'success')
      }
      setShowModal(false)
      fetchBudgets()
    } catch (err) {
      toast(err.response?.data?.message || 'Failed to save budget', 'error')
    }
  }

  const handleDelete = async (id) => {
    try {
      await api.delete(`/budgets/${id}`)
      toast('Budget deleted', 'success')
      fetchBudgets()
    } catch {
      toast('Failed to delete budget', 'error')
    }
  }

  const getProgressColor = (percent) => {
    if (percent >= 90) return 'from-red-500 to-pink-500'
    if (percent >= 70) return 'from-amber-500 to-orange-500'
    return 'from-emerald-500 to-teal-500'
  }

  if (loading) {
    return (
      <div className="grid md:grid-cols-2 lg:grid-cols-3 gap-6">
        {[1, 2, 3].map((i) => (
          <div key={i} className="glass-card p-6 animate-pulse">
            <div className="h-4 bg-gray-200 dark:bg-gray-700 rounded w-24 mb-4" />
            <div className="h-8 bg-gray-200 dark:bg-gray-700 rounded w-32 mb-4" />
            <div className="h-3 bg-gray-200 dark:bg-gray-700 rounded w-full" />
          </div>
        ))}
      </div>
    )
  }

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold">Budgets</h1>
          <p className="text-gray-500 dark:text-gray-400 mt-1">{budgets.length} monthly budgets</p>
        </div>
        <button onClick={openAdd} className="btn-primary flex items-center gap-2">
          <Plus className="w-4 h-4" /> New Budget
        </button>
      </div>

      {budgets.length === 0 ? (
        <div className="glass-card p-12 text-center text-gray-400">
          <Wallet className="w-12 h-12 mx-auto mb-4 opacity-50" />
          <p>No budgets set yet</p>
          <p className="text-sm mt-1">Create a monthly budget to start tracking your spending limits</p>
        </div>
      ) : (
        <div className="grid md:grid-cols-2 lg:grid-cols-3 gap-6">
          {budgets.sort((a, b) => b.year - a.year || b.month - a.month).map((budget) => {
            const percent = budget.budgetLimit > 0 ? Math.min((budget.totalExpense / budget.budgetLimit) * 100, 100) : 0

            return (
              <div key={budget.id} className="glass-card p-6 group">
                <div className="flex items-center justify-between mb-4">
                  <div>
                    <h3 className="font-semibold text-lg">{MONTHS[budget.month - 1]} {budget.year}</h3>
                  </div>
                  <div className="flex gap-1 opacity-0 group-hover:opacity-100 transition-opacity">
                    <button onClick={() => openEdit(budget)} className="p-2 rounded-lg hover:bg-gray-100 dark:hover:bg-gray-800">
                      <Pencil className="w-4 h-4 text-gray-500" />
                    </button>
                    <button onClick={() => handleDelete(budget.id)} className="p-2 rounded-lg hover:bg-red-50 dark:hover:bg-red-900/20">
                      <Trash2 className="w-4 h-4 text-red-500" />
                    </button>
                  </div>
                </div>

                <div className="space-y-3">
                  <div className="flex justify-between text-sm">
                    <span className="text-gray-500">Spent</span>
                    <span className="font-medium">${budget.totalExpense.toFixed(2)}</span>
                  </div>
                  <div className="flex justify-between text-sm">
                    <span className="text-gray-500">Limit</span>
                    <span className="font-medium">${budget.budgetLimit.toFixed(2)}</span>
                  </div>

                  {/* Progress bar */}
                  <div className="h-3 bg-gray-100 dark:bg-gray-800 rounded-full overflow-hidden">
                    <div
                      className={`h-full bg-gradient-to-r ${getProgressColor(percent)} rounded-full transition-all duration-500`}
                      style={{ width: `${percent}%` }}
                    />
                  </div>

                  <div className="flex justify-between text-sm">
                    <span className="text-gray-500">{percent.toFixed(0)}% used</span>
                    <span className={`font-semibold ${budget.remainingBudget >= 0 ? 'text-emerald-500' : 'text-red-500'}`}>
                      ${budget.remainingBudget.toFixed(2)} left
                    </span>
                  </div>
                </div>
              </div>
            )
          })}
        </div>
      )}

      {/* Modal */}
      {showModal && (
        <div className="fixed inset-0 bg-black/50 z-50 flex items-center justify-center p-4" onClick={() => setShowModal(false)}>
          <div className="glass-card p-6 w-full max-w-md" onClick={(e) => e.stopPropagation()}>
            <div className="flex items-center justify-between mb-6">
              <h2 className="text-lg font-semibold">{editing ? 'Edit Budget' : 'New Budget'}</h2>
              <button onClick={() => setShowModal(false)} className="p-1 rounded-lg hover:bg-gray-100 dark:hover:bg-gray-800">
                <X className="w-5 h-5" />
              </button>
            </div>
            <form onSubmit={handleSubmit} className="space-y-4">
              <div className="grid grid-cols-2 gap-4">
                <div>
                  <label className="block text-sm font-medium mb-1.5">Month</label>
                  <select value={form.month} onChange={(e) => setForm({ ...form, month: e.target.value })} className="input-field">
                    {MONTHS.map((m, i) => <option key={i} value={i + 1}>{m}</option>)}
                  </select>
                  {errors.month && <p className="text-red-500 text-xs mt-1">{errors.month}</p>}
                </div>
                <div>
                  <label className="block text-sm font-medium mb-1.5">Year</label>
                  <input type="number" value={form.year} onChange={(e) => setForm({ ...form, year: e.target.value })} className="input-field" />
                </div>
              </div>
              <div>
                <label className="block text-sm font-medium mb-1.5">Budget Limit ($)</label>
                <input type="number" step="0.01" value={form.budgetLimit} onChange={(e) => setForm({ ...form, budgetLimit: e.target.value })} className="input-field" placeholder="5000.00" />
                {errors.budgetLimit && <p className="text-red-500 text-xs mt-1">{errors.budgetLimit}</p>}
              </div>
              <div className="flex gap-3 pt-2">
                <button type="button" onClick={() => setShowModal(false)} className="flex-1 btn-secondary">Cancel</button>
                <button type="submit" className="flex-1 btn-primary">{editing ? 'Update' : 'Create'}</button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  )
}
