import React, { useState, useEffect, useMemo, useRef } from 'react';
import { BarChart3, ExternalLink, Copy, Trash2, Loader2, LogOut, Link2, Search } from 'lucide-react';
import { toast } from 'sonner';
import Swal from 'sweetalert2';
import AnalyticsModal from './AnalyticsModal';
import { createShortenerLink, getAllLinkes, removeShortLink } from '../Services/Url-service';
import { axiosAPI } from '../Services/Auth';
import '../css/Dashboard.css';

// Set VITE_API_BASE_URL in your frontend .env; falls back to local gateway.
const API_BASE = import.meta.env?.VITE_API_BASE_URL || 'http://localhost:9096';

const stripProtocol = (u) => u.replace(/^https?:\/\//, '');

const DashBoard = () => {
  const [url, setUrl] = useState('');
    const [links, setLinks] = useState([]);
    const [query, setQuery] = useState('');
    const [selectedCode, setSelectedCode] = useState(null);
    const [isLoading, setIsLoading] = useState(true);
    const [isShortening, setIsShortening] = useState(false);
    const inputRef = useRef(null);
  
    useEffect(() => {
      (async () => {
        try {
          const resp = await getAllLinkes();
          setLinks(
            resp.map((item) => ({
              original: item.originalUrl,
              short: `${API_BASE}/${item.shortCode}`,
              code: item.shortCode,
            }))
          );
        } catch (err) {
          console.error('Error loading links:', err);
          toast.error("Couldn't load your links. Refresh to try again.");
        } finally {
          setIsLoading(false);
        }
      })();
    }, []);
  
    // Ctrl/Cmd + K focuses the shorten input
    useEffect(() => {
      const onKey = (e) => {
        if ((e.metaKey || e.ctrlKey) && e.key.toLowerCase() === 'k') {
          e.preventDefault();
          inputRef.current?.focus();
        }
      };
      window.addEventListener('keydown', onKey);
      return () => window.removeEventListener('keydown', onKey);
    }, []);
  
    const filtered = useMemo(() => {
      const q = query.trim().toLowerCase();
      if (!q) return links;
      return links.filter(
        (l) => l.original.toLowerCase().includes(q) || l.code.toLowerCase().includes(q)
      );
    }, [links, query]);
  
    const handleShorten = async (e) => {
      e.preventDefault();
      setIsShortening(true);
      try {
        const short = await createShortenerLink(url);
        setLinks((prev) => [{ original: url, short, code: short.split('/').pop() }, ...prev]);
        setUrl('');
        toast.success('Short link created');
      } catch (err) {
        console.error('Short URL not created:', err);
        toast.error("Couldn't shorten that link. Check the URL and try again.");
      } finally {
        setIsShortening(false);
      }
    };
  
    const copyToClipboard = (text) => {
      navigator.clipboard
        .writeText(text)
        .then(() => toast.success('Link copied'))
        .catch(() => toast.error("Couldn't copy. Copy it manually instead."));
    };
  
    const deleteLink = async (code) => {
      const result = await Swal.fire({
        title: 'Delete this link?',
        text: 'The short link stops working and its analytics are removed.',
        icon: 'warning',
        showCancelButton: true,
        confirmButtonColor: '#b42318',
        cancelButtonColor: '#667085',
        confirmButtonText: 'Delete link',
        cancelButtonText: 'Keep link',
      });
      if (!result.isConfirmed) return;
      try {
        await removeShortLink(code);
        setLinks((prev) => prev.filter((l) => l.code !== code));
        toast.success('Link deleted');
      } catch (err) {
        console.error('Delete failed:', err);
        toast.error("Couldn't delete the link. Try again.");
      }
    };
  
    const handleLogout = async () => {
      try {
        await axiosAPI.post(`${API_BASE}/api/v1/auth/logout`, {}, { withCredentials: true });
        window.location.href = '/login';
      } catch (err) {
        console.error('Logout failed', err);
        toast.error("Couldn't sign out. Try again.");
      }
    };
  
    return (
      <div className="sl-page">
        <header className="sl-nav">
          <div className="sl-nav-inner">
            <div className="sl-brand">
              <span className="sl-brand-mark"><Link2 size={16} /></span>
              SwiftLink
            </div>
            <button onClick={handleLogout} className="sl-btn sl-btn-quiet">
              <LogOut size={16} /> Sign out
            </button>
          </div>
        </header>
  
        <main className="sl-main">
          <section className="sl-hero">
            <h1>Shorten a link</h1>
            <form onSubmit={handleShorten} className="sl-shorten">
              <Link2 size={18} className="sl-shorten-icon" />
              <input
                ref={inputRef}
                type="url"
                placeholder="Paste a long URL, e.g. https://example.com/very/long/path"
                value={url}
                onChange={(e) => setUrl(e.target.value)}
                aria-label="Long URL to shorten"
                required
              />
              <kbd className="sl-kbd" aria-hidden="true">Ctrl K</kbd>
              <button type="submit" disabled={isShortening} className="sl-btn sl-btn-primary">
                {isShortening ? <Loader2 className="sl-spin" size={18} /> : 'Shorten'}
              </button>
            </form>
          </section>
  
          <section className="sl-list" aria-labelledby="links-title">
            <div className="sl-list-head">
              <h2 id="links-title">
                Your links <span className="sl-count">{links.length}</span>
              </h2>
              {links.length > 0 && (
                <label className="sl-search">
                  <Search size={16} />
                  <input
                    type="search"
                    placeholder="Search links"
                    value={query}
                    onChange={(e) => setQuery(e.target.value)}
                    aria-label="Search your links"
                  />
                </label>
              )}
            </div>
  
            {isLoading ? (
              <ul className="sl-rows" aria-busy="true">
                {[0, 1, 2].map((i) => (
                  <li key={i} className="sl-row sl-skeleton">
                    <div className="sl-bar sl-bar-lg" />
                    <div className="sl-bar sl-bar-sm" />
                  </li>
                ))}
              </ul>
            ) : links.length === 0 ? (
              <div className="sl-empty">
                <p className="sl-empty-title">No links yet</p>
                <p>Paste a URL above to create your first short link.</p>
              </div>
            ) : filtered.length === 0 ? (
              <div className="sl-empty">
                <p className="sl-empty-title">No matches</p>
                <p>Nothing matches "{query}". Try a different search.</p>
              </div>
            ) : (
              <ul className="sl-rows">
                {filtered.map((link) => (
                  <li key={link.code} className="sl-row">
                    <div className="sl-row-main">
                      <div className="sl-short">
                        <a href={link.short} target="_blank" rel="noreferrer">
                          {stripProtocol(link.short)}
                        </a>
                        <button
                          onClick={() => copyToClipboard(link.short)}
                          className="sl-icon-btn"
                          aria-label="Copy short link"
                          title="Copy short link"
                        >
                          <Copy size={15} />
                        </button>
                      </div>
                      <p className="sl-original" title={link.original}>{link.original}</p>
                    </div>
  
                    <div className="sl-row-actions">
                      <button className="sl-btn sl-btn-outline" onClick={() => setSelectedCode(link.code)}>
                        <BarChart3 size={15} /> Analytics
                      </button>
                      <a
                        className="sl-icon-btn"
                        href={link.short}
                        target="_blank"
                        rel="noreferrer"
                        aria-label="Open link in new tab"
                        title="Open link"
                      >
                        <ExternalLink size={17} />
                      </a>
                      <button
                        className="sl-icon-btn sl-danger"
                        onClick={() => deleteLink(link.code)}
                        aria-label="Delete link"
                        title="Delete link"
                      >
                        <Trash2 size={17} />
                      </button>
                    </div>
                  </li>
                ))}
              </ul>
            )}
          </section>
        </main>
  
        {selectedCode && (
          <AnalyticsModal shortCode={selectedCode} onClose={() => setSelectedCode(null)} />
        )}
      </div>
    );
  };

export default DashBoard;