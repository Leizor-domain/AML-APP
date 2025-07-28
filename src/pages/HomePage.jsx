import React, { useState } from 'react';
import { AppBar, Toolbar, Typography, Button, Box, Container, Grid, TextField, Paper, IconButton, Fade, InputAdornment } from '@mui/material';
import { Email, Facebook, Twitter, LinkedIn, Instagram, LocationOn, Copyright } from '@mui/icons-material';
import { useNavigate } from 'react-router-dom';

// --- NavBar ---
function PublicNavBar() {
  const navigate = useNavigate();
  return (
    <AppBar position="sticky" elevation={0} sx={{ background: 'linear-gradient(90deg, #0f2027 0%, #2c5364 100%)', width: '100vw', left: 0 }}>
      <Toolbar sx={{ px: { xs: 2, md: 4 } }}>
        <Typography variant="h6" sx={{ flexGrow: 1, fontWeight: 700, letterSpacing: 1 }}>
          AML Platform
        </Typography>
        <Button color="inherit" variant="outlined" sx={{ borderRadius: 2, fontWeight: 600 }} onClick={() => navigate('/login')}>
          Login
        </Button>
      </Toolbar>
    </AppBar>
  );
}

// --- Hero Section ---
function HeroSection() {
  return (
    <Box
      sx={{
        minHeight: { xs: 400, md: 520 },
        width: '100vw',
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center',
        background: 'linear-gradient(120deg, #2c5364 0%, #203a43 50%, #0f2027 100%)',
        color: 'white',
        position: 'relative',
        overflow: 'hidden',
        px: 0,
        mx: 0,
      }}
    >
      {/* Animated background graphic */}
      <Box
        sx={{
          position: 'absolute',
          top: '-80px',
          right: '-120px',
          width: { xs: 220, md: 400 },
          height: { xs: 220, md: 400 },
          background: 'radial-gradient(circle, #00c6ff 0%, #0072ff 80%, transparent 100%)',
          opacity: 0.25,
          filter: 'blur(8px)',
          animation: 'fadeIn 2s',
        }}
      />
      <Box sx={{ zIndex: 2, textAlign: 'center', width: '100vw', px: 0 }}>
        <Fade in timeout={1200}>
          <Box>
            <Typography variant="h3" sx={{ fontWeight: 800, mb: 2, letterSpacing: 1 }}>
              Transforming Anti-Money Laundering with Intelligence
            </Typography>
            <Typography variant="h6" sx={{ mb: 4, color: 'rgba(255,255,255,0.85)' }}>
              Next-generation AML platform for secure, automated, and intelligent financial crime detection.
            </Typography>
            <Button
              variant="contained"
              size="large"
              sx={{
                background: 'linear-gradient(90deg, #00c6ff 0%, #0072ff 100%)',
                color: 'white',
                fontWeight: 700,
                borderRadius: 3,
                px: 4,
                boxShadow: 3,
                textTransform: 'none',
              }}
              href="#about"
            >
              Learn More
            </Button>
          </Box>
        </Fade>
      </Box>
    </Box>
  );
}

// --- About Us Section ---
function AboutUsSection() {
  return (
    <Box id="about" sx={{ py: { xs: 6, md: 10 }, background: 'rgba(20,30,40,0.7)', backdropFilter: 'blur(6px)', width: '100vw' }}>
      <Box sx={{ maxWidth: '900px', margin: '0 auto', px: { xs: 2, md: 4 } }}>
        <Typography variant="h4" sx={{ fontWeight: 700, mb: 2, textAlign: 'center', color: '#fff' }}>
          About Us
        </Typography>
        <Typography variant="body1" sx={{ mb: 4, textAlign: 'center', color: '#cfd8dc' }}>
          <b>AML Platform</b> is dedicated to empowering financial institutions with advanced, AI-driven anti-money laundering solutions. Our mission is to deliver automated, intelligent, and scalable tools that help organizations detect, prevent, and report financial crimes efficiently. With a team of compliance experts, data scientists, and engineers, we combine regulatory expertise with cutting-edge technology to stay ahead of evolving threats.<br/><br/>
          <b>Core Values:</b> Integrity, Innovation, Security, and Compliance.<br/>
          <b>Key Features:</b> Real-time Transaction Monitoring, Sanction Screening, Alert Management, Behavioral Analytics, and Regulatory Reporting.
        </Typography>
        <Box sx={{ mt: 6 }}>
          <Typography variant="h5" sx={{ fontWeight: 700, mb: 2, color: '#fff' }}>
            Fraud Prevention Blog
          </Typography>
          <Paper elevation={6} sx={{ p: 3, borderRadius: 3, background: 'rgba(30,40,50,0.85)', color: '#e3f2fd' }}>
            <Typography variant="h6" sx={{ fontWeight: 600, mb: 1, color: '#90caf9' }}>
              The Future of Fraud Prevention: AI & Automation
            </Typography>
            <Typography variant="body2" sx={{ mb: 2 }}>
              <b>By AML Platform Team | July 2024</b>
            </Typography>
            <Typography variant="body1" sx={{ color: '#b0bec5' }}>
              As financial crime grows more sophisticated, so must our defenses. Modern AML systems leverage artificial intelligence and automation to analyze vast amounts of transaction data in real time, identifying suspicious patterns that would be impossible for humans to spot alone. By integrating machine learning, behavioral analytics, and global sanction screening, organizations can not only detect fraud faster but also reduce false positives and streamline compliance workflows.<br/><br/>
              At AML Platform, we are committed to continuous innovation—empowering our clients to stay ahead of emerging threats and regulatory changes. Our latest release introduces adaptive risk scoring, automated alert triage, and seamless integration with global watchlists. The future of fraud prevention is here—and it’s intelligent, automated, and always evolving.
            </Typography>
          </Paper>
        </Box>
      </Box>
    </Box>
  );
}

// --- Contact Us Section ---
function ContactUsSection() {
  const [form, setForm] = useState({ name: '', email: '', message: '' });
  const [success, setSuccess] = useState(false);
  const [error, setError] = useState('');

  const handleChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value });
    setError('');
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    if (!form.name.trim() || !form.email.trim() || !form.message.trim()) {
      setError('Please fill in all fields.');
      return;
    }
    setSuccess(true);
    setTimeout(() => setSuccess(false), 3000);
    setForm({ name: '', email: '', message: '' });
  };

  return (
    <Box id="contact" sx={{ py: { xs: 6, md: 10 }, background: 'rgba(20,30,40,0.7)', backdropFilter: 'blur(6px)', width: '100vw' }}>
      <Box sx={{ maxWidth: '600px', margin: '0 auto', px: { xs: 2, md: 4 } }}>
        <Typography variant="h4" sx={{ fontWeight: 700, mb: 2, textAlign: 'center', color: '#fff' }}>
          Contact Us
        </Typography>
        <Typography variant="body1" sx={{ mb: 4, textAlign: 'center', color: '#cfd8dc' }}>
          Have questions or want to learn more? Send us a message below.
        </Typography>
        <Paper elevation={2} sx={{ p: 4, borderRadius: 3, background: 'rgba(30,40,50,0.85)' }}>
          <form onSubmit={handleSubmit}>
            <Grid container spacing={2}>
              <Grid item xs={12}>
                <TextField label="Name" name="name" fullWidth variant="outlined" autoComplete="name" value={form.name} onChange={handleChange} sx={{ input: { color: '#fff' } }} InputLabelProps={{ style: { color: '#b0bec5' } }} />
              </Grid>
              <Grid item xs={12}>
                <TextField label="Email" name="email" fullWidth variant="outlined" autoComplete="email" type="email" value={form.email} onChange={handleChange} sx={{ input: { color: '#fff' } }} InputLabelProps={{ style: { color: '#b0bec5' } }} />
              </Grid>
              <Grid item xs={12}>
                <TextField label="Message" name="message" fullWidth variant="outlined" multiline minRows={4} value={form.message} onChange={handleChange} sx={{ input: { color: '#fff' } }} InputLabelProps={{ style: { color: '#b0bec5' } }} />
              </Grid>
              {error && <Grid item xs={12}><Typography color="error">{error}</Typography></Grid>}
              {success && <Grid item xs={12}><Typography color="success.main">Message sent! Thank you.</Typography></Grid>}
              <Grid item xs={12}>
                <Button variant="contained" color="primary" fullWidth sx={{ borderRadius: 2, fontWeight: 600 }} type="submit">
                  Send Message
                </Button>
              </Grid>
            </Grid>
          </form>
        </Paper>
      </Box>
    </Box>
  );
}

// --- Newsletter Subscription Section ---
function NewsletterSection() {
  const [email, setEmail] = useState('');
  const [success, setSuccess] = useState(false);
  const [error, setError] = useState('');

  const handleSubscribe = (e) => {
    e.preventDefault();
    if (!email.trim()) {
      setError('Please enter your email.');
      return;
    }
    setSuccess(true);
    setTimeout(() => setSuccess(false), 3000);
    setEmail('');
    setError('');
  };

  return (
    <Box sx={{ py: { xs: 6, md: 8 }, background: 'rgba(20,30,40,0.7)', backdropFilter: 'blur(6px)', width: '100vw' }}>
      <Box sx={{ maxWidth: '600px', margin: '0 auto', px: { xs: 2, md: 4 } }}>
        <Paper elevation={3} sx={{ p: 4, borderRadius: 3, textAlign: 'center', background: 'rgba(30,40,50,0.85)' }}>
          <Typography variant="h5" sx={{ fontWeight: 700, mb: 2, color: '#fff' }}>
            Stay Updated
          </Typography>
          <Typography variant="body2" sx={{ mb: 3, color: '#cfd8dc' }}>
            Subscribe to our newsletter for the latest AML insights and platform updates.
          </Typography>
          <Box component="form" onSubmit={handleSubscribe} sx={{ display: 'flex', gap: 2, justifyContent: 'center', flexWrap: 'wrap' }}>
            <TextField
              type="email"
              placeholder="Your email address"
              variant="outlined"
              size="small"
              value={email}
              onChange={e => { setEmail(e.target.value); setError(''); }}
              sx={{ minWidth: 220, background: 'rgba(20,30,40,0.7)', borderRadius: 2, input: { color: '#fff' } }}
              InputLabelProps={{ style: { color: '#b0bec5' } }}
              InputProps={{
                startAdornment: (
                  <InputAdornment position="start">
                    <Email color="primary" />
                  </InputAdornment>
                ),
              }}
            />
            <Button variant="contained" color="primary" sx={{ borderRadius: 2, fontWeight: 600 }} type="submit">
              Subscribe
            </Button>
          </Box>
          {error && <Typography color="error" sx={{ mt: 2 }}>{error}</Typography>}
          {success && <Typography color="success.main" sx={{ mt: 2 }}>Subscribed successfully!</Typography>}
        </Paper>
      </Box>
    </Box>
  );
}

// --- Footer ---
function Footer() {
  return (
    <Box sx={{ background: 'linear-gradient(90deg, #0f2027 0%, #2c5364 100%)', color: 'white', py: 4, mt: 6, width: '100vw' }}>
      <Box sx={{ maxWidth: '1200px', margin: '0 auto', px: { xs: 2, md: 4 } }}>
        <Grid container spacing={2} alignItems="center" justifyContent="space-between">
          <Grid item xs={12} md={4} sx={{ textAlign: { xs: 'center', md: 'left' } }}>
            <Typography variant="body2" sx={{ mb: 1 }}>
              Contact: <a href="mailto:leizordev@outlook.com" style={{ color: '#00c6ff', textDecoration: 'none' }}>leizordev@outlook.com</a>
            </Typography>
            <Box>
              <IconButton color="inherit" size="small" aria-label="Facebook" sx={{ mx: 0.5 }}><Facebook /></IconButton>
              <IconButton color="inherit" size="small" aria-label="Twitter" sx={{ mx: 0.5 }}><Twitter /></IconButton>
              <IconButton color="inherit" size="small" aria-label="LinkedIn" sx={{ mx: 0.5 }}><LinkedIn /></IconButton>
              <IconButton color="inherit" size="small" aria-label="Instagram" sx={{ mx: 0.5 }}><Instagram /></IconButton>
            </Box>
          </Grid>
          <Grid item xs={12} md={4} sx={{ textAlign: 'center', mt: { xs: 2, md: 0 } }}>
            <Typography variant="body2" sx={{ opacity: 0.8 }}>
              &copy; {new Date().getFullYear()} AML Platform. All rights reserved.
            </Typography>
          </Grid>
          <Grid item xs={12} md={4} sx={{ textAlign: { xs: 'center', md: 'right' }, mt: { xs: 2, md: 0 } }}>
            <Button color="inherit" size="small" sx={{ textTransform: 'none', opacity: 0.8, mr: 1 }} disabled>Terms of Use</Button>
            <Button color="inherit" size="small" sx={{ textTransform: 'none', opacity: 0.8 }} disabled>Privacy Policy</Button>
          </Grid>
        </Grid>
      </Box>
    </Box>
  );
}

// --- Main Home Page ---
export default function HomePage() {
  return (
    <Box
      sx={{
        fontFamily: 'Roboto, Inter, Poppins, sans-serif',
        background: 'linear-gradient(120deg, #232526 0%, #414345 100%)',
        position: 'fixed',
        top: 0,
        left: 0,
        width: '100vw',
        height: '100vh',
        minHeight: '100vh',
        overflow: 'auto',
        zIndex: 0,
        m: 0,
        p: 0,
      }}
    >
      <PublicNavBar />
      <HeroSection />
      <AboutUsSection />
      <NewsletterSection />
      <ContactUsSection />
      <Footer />
    </Box>
  );
} 