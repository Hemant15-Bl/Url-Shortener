import React, { useEffect, useMemo, useRef, useState, useCallback } from 'react';
import { PieChart, Pie, Tooltip, ResponsiveContainer } from 'recharts';
import { X, ChevronDown, Loader2 } from 'lucide-react';
import { loadLinkHistory, loadLinkStats } from '../Services/Analytic-service';
import '../css/AnalyticsModal.css';

const COLORS = ['#0f766e', '#2563eb', '#d97706', '#7c3aed', '#be123c', '#64708a'];

const formatTime = (value) =>
  value ? new Date(value).toLocaleString(undefined, { dateStyle: 'medium', timeStyle: 'short' }) : '-';

// Tidy raw user-agent / network values for display
const formatIp = (ip) => (ip === '0:0:0:0:0:0:0:1' || ip === '::1' ? 'localhost' : ip || '-');
const formatOs = (os) => {
  if (!os || os.includes('??')) return 'Unknown OS';
  return os.replace(/>=\s*(\d+)/, '$1+');
};

const AnalyticsModal = ({ shortCode, onClose }) => {
  const [stats, setStats] = useState(null);
  const [history, setHistory] = useState([]);
  const [page, setPage] = useState(0);
  const [hasMore, setHasMore] = useState(false);
  const [loadingStats, setLoadingStats] = useState(true);
  const [loadingMore, setLoadingMore] = useState(false);
  const [error, setError] = useState(null);
  const closeRef = useRef(null);

  const fetchHistory = useCallback(
    async (pageToFetch) => {
      setLoadingMore(true);
      try {
        const res = await loadLinkHistory(pageToFetch, shortCode);
        setHistory((prev) => (pageToFetch === 0 ? res.content : [...prev, ...res.content]));
        setHasMore(!res.last);
        setPage(pageToFetch + 1);
      } catch (err) {
        console.error('History fetch error:', err);
        setError("Couldn't load click activity.");
      } finally {
        setLoadingMore(false);
      }
    },
    [shortCode]
  );

  useEffect(() => {
    let cancelled = false;
    (async () => {
      setLoadingStats(true);
      setError(null);
      try {
        const data = await loadLinkStats(shortCode);
        if (cancelled) return;
        const chartData = (data.chartData || []).map((item, i) => ({
          ...item,
          fill: COLORS[i % COLORS.length],
        }));
        setStats({ ...data, chartData });
        fetchHistory(0);
      } catch (err) {
        console.error('Initial fetch error:', err);
        if (!cancelled) setError("Couldn't load analytics for this link.");
      } finally {
        if (!cancelled) setLoadingStats(false);
      }
    })();
    return () => { cancelled = true; };
  }, [shortCode, fetchHistory]);

  // Escape to close, lock page scroll, focus the close button
  useEffect(() => {
    const onKey = (e) => e.key === 'Escape' && onClose();
    document.addEventListener('keydown', onKey);
    const prev = document.body.style.overflow;
    document.body.style.overflow = 'hidden';
    closeRef.current?.focus();
    return () => {
      document.removeEventListener('keydown', onKey);
      document.body.style.overflow = prev;
    };
  }, [onClose]);

  const { total, topBrowser } = useMemo(() => {
    const data = stats?.chartData || [];
    const sum = data.reduce((s, d) => s + d.value, 0);
    const top = data.reduce((a, b) => (b.value > (a?.value ?? -1) ? b : a), null);
    return { total: sum, topBrowser: top?.name || 'No data' };
  }, [stats]);

  return (
    <div className="sl-modal-overlay" onMouseDown={(e) => e.target === e.currentTarget && onClose()}>
      <div className="sl-modal" role="dialog" aria-modal="true" aria-labelledby="analytics-title">
        <header className="sl-modal-head">
          <div>
            <h2 id="analytics-title">Link analytics</h2>
            <p className="sl-modal-sub">/{shortCode}</p>
          </div>
          <button ref={closeRef} onClick={onClose} className="sl-icon-btn" aria-label="Close analytics">
            <X size={20} />
          </button>
        </header>

        {loadingStats ? (
          <div className="sl-modal-state"><Loader2 className="sl-spin" size={28} /><p>Loading analytics...</p></div>
        ) : !stats ? (
          <div className="sl-modal-state"><p>{error}</p></div>
        ) : (
          <div className="sl-modal-body">
            <div className="sl-metrics">
              <div className="sl-metric">
                <span>Total clicks</span>
                <strong>{stats.totalClick ?? 0}</strong>
              </div>
              <div className="sl-metric">
                <span>Top browser</span>
                <strong>{topBrowser}</strong>
              </div>
              <div className="sl-metric">
                <span>Last click</span>
                <strong className="sl-metric-small">{history[0] ? formatTime(history[0].clickedAt || history[0].timestamp) : '-'}</strong>
              </div>
            </div>

            <section className="sl-panel">
              <h3>Browsers</h3>
              {stats.chartData.length > 0 ? (
                <div className="sl-browsers">
                  <div className="sl-donut">
                    <ResponsiveContainer width="100%" height="100%">
                      <PieChart>
                        <Pie data={stats.chartData} dataKey="value" nameKey="name" innerRadius="62%" outerRadius="92%" paddingAngle={3} stroke="none" />
                        <Tooltip />
                      </PieChart>
                    </ResponsiveContainer>
                  </div>
                  <ul className="sl-legend">
                    {stats.chartData.map((d) => (
                      <li key={d.name}>
                        <span className="sl-dot" style={{ background: d.fill }} />
                        <span className="sl-legend-name">{d.name}</span>
                        <span className="sl-legend-val">
                          {d.value} <em>({total ? Math.round((d.value / total) * 100) : 0}%)</em>
                        </span>
                      </li>
                    ))}
                  </ul>
                </div>
              ) : (
                <p className="sl-muted">No browser data yet. Clicks on this link will appear here.</p>
              )}
            </section>

            <section className="sl-panel">
              <h3>Recent clicks</h3>
              {history.length === 0 && !loadingMore ? (
                <p className="sl-muted">No clicks recorded yet.</p>
              ) : (
                <div className="sl-table-wrap">
                  <table className="sl-table">
                    <thead>
                      <tr><th>Time</th><th>IP address</th><th>Browser</th><th>System</th></tr>
                    </thead>
                    <tbody>
                      {history.map((log, i) => (
                        <tr key={log.id ?? `${log.clickedAt || log.timestamp}-${i}`}>
                          <td>{formatTime(log.clickedAt || log.timestamp)}</td>
                          <td>{formatIp(log.ipAddress)}</td>
                          <td>{log.browser || 'Unknown'}</td>
                          <td>{formatOs(log.os)}</td>
                        </tr>
                      ))}
                    </tbody>
                  </table>
                </div>
              )}
              {error && <p className="sl-error">{error}</p>}
              {hasMore && (
                <button onClick={() => fetchHistory(page)} disabled={loadingMore} className="sl-btn sl-btn-outline sl-more">
                  {loadingMore ? <Loader2 className="sl-spin" size={16} /> : <ChevronDown size={16} />}
                  {loadingMore ? 'Loading...' : 'Show more'}
                </button>
              )}
            </section>
          </div>
        )}
      </div>
    </div>
  );
};
export default AnalyticsModal;