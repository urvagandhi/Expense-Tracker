import { useState, useEffect } from 'react'
import api from '../api/axios'
import { useToast } from '../components/Toast'
import { Plus, Pencil, Trash2, Search, X, Receipt, Filter } from 'lucide-react'

const CATEGORIES = ['Food', 'Transport', 'Shopping', 'Entertainment', 'Health', 'Education', 'Bills', 'Other']

export default function Expenses() {
  const [expenses, setExpenses] = useState([])
  const [loading, setLoading] = useState(true)
  const [showModal, setShowModal] = useState(false)
  const [editing, setEditing] = useState(null)
  const [search, setSearch] = useState('')
  const [filterCat, setFilterCat] = useState('')
  const [form, setForm] = useState({ amount: '', category: 'Food', description: '', expenseDate: '' })
  const [errors, setErrors] = useState({})
  const toast = useToast()

  const fetchExpenses = () => {
    api.get('/expenses/all')
      .then((res) => setExpenses(res.data.data || []))
      .finally(() => setLoading(false))
  }

  useEffect(() => { fetchExpenses() }, [])

  const openAdd = () => {
    setEditing(null)
    setForm({ amount: '', category: 'Food', description: '', expenseDate: new Date().toISOString().split('T')[0] })
    setErrors({})
    setShowModal(true)
  }

  const openEdit = (expense) => {
    setEditing(expense)
    setForm({
      amount: expense.amount.toString(),
      category: expense.category,
      description: expense.description,
      expenseDate: expense.expenseDate,
    })
    setErrors({})
    setShowModal(true)
  }

  const validate = () => {
    const errs = {}
    if (!form.amount || parseFloat(form.amount) <= 0) errs.amount = 'Amount must be positive'
    if (!form.description.trim()) errs.description = 'Description is required'
    if (!form.expenseDate) errs.expenseDate = 'Date is required'
    setErrors(errs)
    return Object.keys(errs).length === 0
  }

  const handleSubmit = async (e) => {
    e.preventDefault()
    if (!validate()) return
    const payload = { ...form, amount: parseFloat(form.amount) }
    try {
      if (editing) {
        await api.put(`/expenses/${editing.id}`, payload)
        toast('Expense updated', 'success')
      } else {
        await api.post('/expenses', payload)
        toast('Expense added', 'success')
      }
      setShowModal(false)
      fetchExpenses()
    } catch (err) {
      toast(err.response?.data?.message || 'Failed to save expense', 'error')
    }
  }

  const handleDelete = async (id) => {
    try {
      await api.delete(`/expenses/${id}`)
      toast('Expense deleted', 'success')
      fetchExpenses()
    } catch {
      toast('Failed to delete expense', 'error')
    }
  }

  const filtered = expenses.filter((e) => {
    const matchSearch = e.description.toLowerCase().includes(search.toLowerCase()) || e.category.toLowerCase().includes(search.toLowerCase())
    const matchCat = !filterCat || e.category === filterCat
    return matchSearch && matchCat
  })

  const totalFiltered = filtered.reduce((sum, e) => sum + e.amount, 0)

  if (loading) {
    return (
      <div className="space-y-4">
        {[1, 2, 3, 4, 5].map((i) => (
          <div key={i} className="glass-card p-4 animate-pulse">
            <div className="h-4 bg-gray-200 dark:bg-gray-700 rounded w-3/4" />
          </div>
        ))}
      </div>
    )
  }

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold">Expenses</h1>
          <p className="text-gray-500 dark:text-gray-400 mt-1">{expenses.length} transactions</p>
        </div>
        <button onClick={openAdd} className="btn-primary flex items-center gap-2">
          <Plus className="w-4 h-4" /> Add Expense
        </button>
      </div>

      {/* Filters */}
      <div className="glass-card p-4 flex flex-col sm:flex-row gap-3">
        <div className="relative flex-1">
          <Search className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-gray-400" />
          <input
            type="text"
            placeholder="Search expenses..."
            value={search}
            onChange={(e) => setSearch(e.target.value)}
            className="input-field pl-10 py-2.5"
          />
        </div>
        <div className="relative">
          <Filter className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-gray-400" />
          <select
            value={filterCat}
            onChange={(e) => setFilterCat(e.target.value)}
            className="input-field pl-10 py-2.5 pr-8 appearance-none"
          >
            <option value="">All Categories</option>
            {CATEGORIES.map((c) => <option key={c} value={c}>{c}</option>)}
          </select>
        </div>
      </div>

      {/* Summary bar */}
      <div className="glass-card px-6 py-3 flex items-center justify-between">
        <span className="text-sm text-gray-500">{filtered.length} results</span>
        <span className="font-semibold">Total: <span className="text-red-500">${totalFiltered.toFixed(2)}</span></span>
      </div>

      {/* Expense List */}
      <div className="space-y-3">
        {filtered.length === 0 ? (
          <div className="glass-card p-12 text-center text-gray-400">
            <Receipt className="w-12 h-12 mx-auto mb-4 opacity-50" />
            <p>No expenses found</p>
          </div>
        ) : (
          filtered.map((expense) => (
            <div key={expense.id} className="glass-card p-4 flex items-center justify-between group hover:shadow-lg transition-shadow">
              <div className="flex items-center gap-4">
                <div className="w-11 h-11 rounded-xl bg-gradient-to-br from-indigo-500/10 to-purple-500/10 dark:from-indigo-500/20 dark:to-purple-500/20 flex items-center justify-center">
                  <Receipt className="w-5 h-5 text-indigo-500" />
                </div>
                <div>
                  <p className="font-medium">{expense.description}</p>
                  <p className="text-xs text-gray-500 mt-0.5">{expense.category} &middot; {expense.expenseDate}</p>
                </div>
              </div>
              <div className="flex items-center gap-4">
                <span className="font-semibold text-lg">${expense.amount.toFixed(2)}</span>
                <div className="flex gap-1 opacity-0 group-hover:opacity-100 transition-opacity">
                  <button onClick={() => openEdit(expense)} className="p-2 rounded-lg hover:bg-gray-100 dark:hover:bg-gray-800">
                    <Pencil className="w-4 h-4 text-gray-500" />
                  </button>
                  <button onClick={() => handleDelete(expense.id)} className="p-2 rounded-lg hover:bg-red-50 dark:hover:bg-red-900/20">
                    <Trash2 className="w-4 h-4 text-red-500" />
                  </button>
                </div>
              </div>
            </div>
          ))
        )}
      </div>

      {/* Modal */}
      {showModal && (
        <div className="fixed inset-0 bg-black/50 z-50 flex items-center justify-center p-4" onClick={() => setShowModal(false)}>
          <div className="glass-card p-6 w-full max-w-md" onClick={(e) => e.stopPropagation()}>
            <div className="flex items-center justify-between mb-6">
              <h2 className="text-lg font-semibold">{editing ? 'Edit Expense' : 'Add Expense'}</h2>
              <button onClick={() => setShowModal(false)} className="p-1 rounded-lg hover:bg-gray-100 dark:hover:bg-gray-800">
                <X className="w-5 h-5" />
              </button>
            </div>
            <form onSubmit={handleSubmit} className="space-y-4">
              <div>
                <label className="block text-sm font-medium mb-1.5">Amount</label>
                <input type="number" step="0.01" value={form.amount} onChange={(e) => setForm({ ...form, amount: e.target.value })} className="input-field" placeholder="0.00" />
                {errors.amount && <p className="text-red-500 text-xs mt-1">{errors.amount}</p>}
              </div>
              <div>
                <label className="block text-sm font-medium mb-1.5">Category</label>
                <select value={form.category} onChange={(e) => setForm({ ...form, category: e.target.value })} className="input-field">
                  {CATEGORIES.map((c) => <option key={c} value={c}>{c}</option>)}
                </select>
              </div>
              <div>
                <label className="block text-sm font-medium mb-1.5">Description</label>
                <input type="text" value={form.description} onChange={(e) => setForm({ ...form, description: e.target.value })} className="input-field" placeholder="What did you spend on?" />
                {errors.description && <p className="text-red-500 text-xs mt-1">{errors.description}</p>}
              </div>
              <div>
                <label className="block text-sm font-medium mb-1.5">Date</label>
                <input type="date" value={form.expenseDate} onChange={(e) => setForm({ ...form, expenseDate: e.target.value })} className="input-field" />
                {errors.expenseDate && <p className="text-red-500 text-xs mt-1">{errors.expenseDate}</p>}
              </div>
              <div className="flex gap-3 pt-2">
                <button type="button" onClick={() => setShowModal(false)} className="flex-1 btn-secondary">Cancel</button>
                <button type="submit" className="flex-1 btn-primary">{editing ? 'Update' : 'Add'}</button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  )
}
