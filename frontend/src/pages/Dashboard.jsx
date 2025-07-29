import { useState, useEffect } from 'react'
import { useAuth } from '../context/AuthContext'
import api from '../api/axios'
import { DollarSign, TrendingDown, Wallet, Receipt } from 'lucide-react'
import { PieChart, Pie, Cell, BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer } from 'recharts'

const COLORS = ['#6366f1', '#8b5cf6', '#a855f7', '#d946ef', '#ec4899', '#f43f5e', '#f97316', '#eab308', '#22c55e', '#14b8a6']

export default function Dashboard() {
  const { user } = useAuth()
  const [expenses, setExpenses] = useState([])
  const [budgets, setBudgets] = useState([])
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    Promise.all([
      api.get('/expenses/all'),
      api.get('/budgets'),
    ]).then(([expRes, budRes]) => {
      setExpenses(expRes.data.data || [])
      setBudgets(budRes.data.data || [])
    }).finally(() => setLoading(false))
  }, [])

  const totalExpenses = expenses.reduce((sum, e) => sum + e.amount, 0)
  const totalBudget = budgets.reduce((sum, b) => sum + b.budgetLimit, 0)
  const totalRemaining = budgets.reduce((sum, b) => sum + b.remainingBudget, 0)

  const categoryData = expenses.reduce((acc, e) => {
    const existing = acc.find((c) => c.name === e.category)
    if (existing) existing.value += e.amount
    else acc.push({ name: e.category, value: e.amount })
    return acc
  }, [])

  const monthlyData = expenses.reduce((acc, e) => {
    const month = e.expenseDate?.substring(0, 7)
    const existing = acc.find((m) => m.month === month)
    if (existing) existing.amount += e.amount
    else acc.push({ month, amount: e.amount })
    return acc
  }, []).sort((a, b) => a.month.localeCompare(b.month)).slice(-6)

  const statCards = [
    { label: 'Total Expenses', value: `$${totalExpenses.toFixed(2)}`, icon: TrendingDown, color: 'from-red-500 to-pink-500' },
    { label: 'Total Budget', value: `$${totalBudget.toFixed(2)}`, icon: Wallet, color: 'from-indigo-500 to-purple-500' },
    { label: 'Remaining', value: `$${totalRemaining.toFixed(2)}`, icon: DollarSign, color: 'from-emerald-500 to-teal-500' },
    { label: 'Transactions', value: expenses.length, icon: Receipt, color: 'from-amber-500 to-orange-500' },
  ]

  if (loading) {
    return (
      <div className="space-y-6">
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
          {[1, 2, 3, 4].map((i) => (
            <div key={i} className="glass-card p-6 animate-pulse">
              <div className="h-4 bg-gray-200 dark:bg-gray-700 rounded w-24 mb-3" />
              <div className="h-8 bg-gray-200 dark:bg-gray-700 rounded w-32" />
            </div>
          ))}
        </div>
      </div>
    )
  }

  return (
    <div className="space-y-8">
      <div>
        <h1 className="text-2xl font-bold">Welcome back, {user?.username}</h1>
        <p className="text-gray-500 dark:text-gray-400 mt-1">Here's your financial overview</p>
      </div>

      {/* Stat Cards */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
        {statCards.map((card) => (
          <div key={card.label} className="glass-card p-6">
            <div className="flex items-center justify-between mb-4">
              <span className="text-sm text-gray-500 dark:text-gray-400">{card.label}</span>
              <div className={`w-10 h-10 rounded-xl bg-gradient-to-br ${card.color} flex items-center justify-center`}>
                <card.icon className="w-5 h-5 text-white" />
              </div>
            </div>
            <p className="text-2xl font-bold">{card.value}</p>
          </div>
        ))}
      </div>

      {/* Charts */}
      <div className="grid lg:grid-cols-2 gap-6">
        {/* Monthly Bar Chart */}
        <div className="glass-card p-6">
          <h3 className="text-lg font-semibold mb-6">Monthly Spending</h3>
          {monthlyData.length > 0 ? (
            <ResponsiveContainer width="100%" height={300}>
              <BarChart data={monthlyData}>
                <CartesianGrid strokeDasharray="3 3" stroke="#e5e7eb" />
                <XAxis dataKey="month" tick={{ fontSize: 12 }} />
                <YAxis tick={{ fontSize: 12 }} />
                <Tooltip
                  contentStyle={{
                    backgroundColor: 'rgba(255,255,255,0.95)',
                    border: 'none',
                    borderRadius: '12px',
                    boxShadow: '0 4px 20px rgba(0,0,0,0.1)',
                  }}
                  formatter={(value) => [`$${value.toFixed(2)}`, 'Amount']}
                />
                <Bar dataKey="amount" fill="url(#barGradient)" radius={[8, 8, 0, 0]} />
                <defs>
                  <linearGradient id="barGradient" x1="0" y1="0" x2="0" y2="1">
                    <stop offset="0%" stopColor="#6366f1" />
                    <stop offset="100%" stopColor="#a855f7" />
                  </linearGradient>
                </defs>
              </BarChart>
            </ResponsiveContainer>
          ) : (
            <div className="h-[300px] flex items-center justify-center text-gray-400">No expenses yet</div>
          )}
        </div>

        {/* Category Pie Chart */}
        <div className="glass-card p-6">
          <h3 className="text-lg font-semibold mb-6">Spending by Category</h3>
          {categoryData.length > 0 ? (
            <ResponsiveContainer width="100%" height={300}>
              <PieChart>
                <Pie
                  data={categoryData}
                  cx="50%"
                  cy="50%"
                  innerRadius={60}
                  outerRadius={100}
                  paddingAngle={4}
                  dataKey="value"
                >
                  {categoryData.map((_, i) => (
                    <Cell key={i} fill={COLORS[i % COLORS.length]} />
                  ))}
                </Pie>
                <Tooltip formatter={(value) => `$${value.toFixed(2)}`} />
              </PieChart>
            </ResponsiveContainer>
          ) : (
            <div className="h-[300px] flex items-center justify-center text-gray-400">No expenses yet</div>
          )}
          {categoryData.length > 0 && (
            <div className="flex flex-wrap gap-3 mt-4">
              {categoryData.map((cat, i) => (
                <div key={cat.name} className="flex items-center gap-2 text-sm">
                  <div className="w-3 h-3 rounded-full" style={{ backgroundColor: COLORS[i % COLORS.length] }} />
                  <span className="text-gray-600 dark:text-gray-400">{cat.name}</span>
                </div>
              ))}
            </div>
          )}
        </div>
      </div>

      {/* Recent Expenses */}
      <div className="glass-card p-6">
        <h3 className="text-lg font-semibold mb-4">Recent Expenses</h3>
        {expenses.length > 0 ? (
          <div className="divide-y divide-gray-100 dark:divide-gray-800">
            {expenses.slice(0, 5).map((expense) => (
              <div key={expense.id} className="flex items-center justify-between py-3">
                <div className="flex items-center gap-4">
                  <div className="w-10 h-10 rounded-xl bg-gray-100 dark:bg-gray-800 flex items-center justify-center">
                    <Receipt className="w-5 h-5 text-gray-500" />
                  </div>
                  <div>
                    <p className="font-medium text-sm">{expense.description}</p>
                    <p className="text-xs text-gray-500">{expense.category} - {expense.expenseDate}</p>
                  </div>
                </div>
                <span className="font-semibold text-red-500">-${expense.amount.toFixed(2)}</span>
              </div>
            ))}
          </div>
        ) : (
          <p className="text-gray-400 text-center py-8">No expenses recorded yet</p>
        )}
      </div>
    </div>
  )
}
