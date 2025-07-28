import React from 'react';
import { AppBar, Toolbar, Typography, Button, Box, Container, Grid, TextField, Paper, IconButton, Fade, InputAdornment } from '@mui/material';
import { Email, Facebook, Twitter, LinkedIn, Instagram, LocationOn, Copyright } from '@mui/icons-material';
import { useNavigate } from 'react-router-dom';

// --- NavBar ---
function PublicNavBar() {
  const navigate = useNavigate();
  return (
    <AppBar position="sticky" elevation={0} sx={{ background: 'linear-gradient(90deg, #0f2027 0%, #2c5364 100%)' }}>
      <Toolbar>
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
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center',
        background: 'linear-gradient(120deg, #2c5364 0%, #203a43 50%, #0f2027 100%)',
        color: 'white',
        position: 'relative',
        overflow: 'hidden',
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
      <Container sx={{ zIndex: 2, textAlign: 'center' }}>
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
      </Container>
    </Box>
  );
}

// --- About Us Section ---
function AboutUsSection() {
  return (
    <Box id="about" sx={{ py: { xs: 6, md: 10 }, background: 'white' }}>
      <Container maxWidth="md">
        <Typography variant="h4" sx={{ fontWeight: 700, mb: 2, textAlign: 'center', color: '#203a43' }}>
          About Us
        </Typography>
        <Typography variant="body1" sx={{ mb: 4, textAlign: 'center', color: '#444' }}>
          We are a team of passionate technologists and compliance experts dedicated to revolutionizing anti-money laundering. Our AML Engine leverages advanced analytics and automation to help financial institutions stay ahead of financial crime.
        </Typography>
        <Grid container spacing={4} justifyContent="center">
          <Grid item xs={12} md={4}>
            <Paper elevation={3} sx={{ p: 3, borderRadius: 3, textAlign: 'center', minHeight: 180 }}>
              <Typography variant="h6" sx={{ fontWeight: 600, color: '#0072ff', mb: 1 }}>
                Transaction Monitoring
              </Typography>
              <Typography variant="body2" color="text.secondary">
                Real-time detection of suspicious transactions using dynamic rules and behavioral analytics.
              </Typography>
            </Paper>
          </Grid>
          <Grid item xs={12} md={4}>
            <Paper elevation={3} sx={{ p: 3, borderRadius: 3, textAlign: 'center', minHeight: 180 }}>
              <Typography variant="h6" sx={{ fontWeight: 600, color: '#0072ff', mb: 1 }}>
                Sanction Screening
              </Typography>
              <Typography variant="body2" color="text.secondary">
                Automated screening against global sanction lists to ensure regulatory compliance.
              </Typography>
            </Paper>
          </Grid>
          <Grid item xs={12} md={4}>
            <Paper elevation={3} sx={{ p: 3, borderRadius: 3, textAlign: 'center', minHeight: 180 }}>
              <Typography variant="h6" sx={{ fontWeight: 600, color: '#0072ff', mb: 1 }}>
                Alert Management
              </Typography>
              <Typography variant="body2" color="text.secondary">
                Centralized dashboard for managing, investigating, and resolving AML alerts efficiently.
              </Typography>
            </Paper>
          </Grid>
        </Grid>
      </Container>
    </Box>
  );
}

// --- Contact Us Section ---
function ContactUsSection() {
  return (
    <Box id="contact" sx={{ py: { xs: 6, md: 10 }, background: 'linear-gradient(90deg, #e0eafc 0%, #cfdef3 100%)' }}>
      <Container maxWidth="sm">
        <Typography variant="h4" sx={{ fontWeight: 700, mb: 2, textAlign: 'center', color: '#203a43' }}>
          Contact Us
        </Typography>
        <Typography variant="body1" sx={{ mb: 4, textAlign: 'center', color: '#444' }}>
          Have questions or want to learn more? Send us a message below.
        </Typography>
        <Paper elevation={2} sx={{ p: 4, borderRadius: 3 }}>
          <Grid container spacing={2}>
            <Grid item xs={12}>
              <TextField label="Name" fullWidth variant="outlined" autoComplete="name" />
            </Grid>
            <Grid item xs={12}>
              <TextField label="Email" fullWidth variant="outlined" autoComplete="email" type="email" />
            </Grid>
            <Grid item xs={12}>
              <TextField label="Message" fullWidth variant="outlined" multiline minRows={4} />
            </Grid>
            <Grid item xs={12}>
              <Button variant="contained" color="primary" fullWidth sx={{ borderRadius: 2, fontWeight: 600 }} disabled>
                Send Message
              </Button>
            </Grid>
          </Grid>
        </Paper>
      </Container>
    </Box>
  );
}

// --- Newsletter Subscription Section ---
function NewsletterSection() {
  return (
    <Box sx={{ py: { xs: 6, md: 8 }, background: 'white' }}>
      <Container maxWidth="sm">
        <Paper elevation={3} sx={{ p: 4, borderRadius: 3, textAlign: 'center' }}>
          <Typography variant="h5" sx={{ fontWeight: 700, mb: 2, color: '#203a43' }}>
            Stay Updated
          </Typography>
          <Typography variant="body2" sx={{ mb: 3, color: '#444' }}>
            Subscribe to our newsletter for the latest AML insights and platform updates.
          </Typography>
          <Box component="form" sx={{ display: 'flex', gap: 2, justifyContent: 'center', flexWrap: 'wrap' }}>
            <TextField
              type="email"
              placeholder="Your email address"
              variant="outlined"
              size="small"
              sx={{ minWidth: 220, background: 'white', borderRadius: 2 }}
              InputProps={{
                startAdornment: (
                  <InputAdornment position="start">
                    <Email color="primary" />
                  </InputAdornment>
                ),
              }}
            />
            <Button variant="contained" color="primary" sx={{ borderRadius: 2, fontWeight: 600 }} disabled>
              Subscribe
            </Button>
          </Box>
        </Paper>
      </Container>
    </Box>
  );
}

// --- Footer ---
function Footer() {
  return (
    <Box sx={{ background: 'linear-gradient(90deg, #0f2027 0%, #2c5364 100%)', color: 'white', py: 4, mt: 6 }}>
      <Container maxWidth="lg">
        <Grid container spacing={2} alignItems="center" justifyContent="space-between">
          <Grid item xs={12} md={4} sx={{ textAlign: { xs: 'center', md: 'left' } }}>
            <Typography variant="body2" sx={{ mb: 1 }}>
              Contact: <a href="mailto:info@amlplatform.com" style={{ color: '#00c6ff', textDecoration: 'none' }}>info@amlplatform.com</a>
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
      </Container>
    </Box>
  );
}

// --- Main Home Page ---
export default function HomePage() {
  return (
    <Box sx={{ fontFamily: 'Roboto, Inter, Poppins, sans-serif', background: '#f6fafd' }}>
      <PublicNavBar />
      <HeroSection />
      <AboutUsSection />
      <NewsletterSection />
      <ContactUsSection />
      <Footer />
    </Box>
  );
} 