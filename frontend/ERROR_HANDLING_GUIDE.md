# Guide de Gestion d'Erreurs Frontend - Purple Dog

## Architecture

### Structure des dossiers
```
frontend/src/
├── utils/
│   ├── errorMessages.js      # Messages d'erreur centralisés
│   ├── errorHandler.js        # Service de gestion d'erreurs
│   ├── logger.js              # Service de logging
│   └── apiClient.js           # Client Axios configuré
├── components/errors/
│   ├── ErrorBoundary.jsx      # Boundary React
│   ├── ErrorFallback.jsx      # UI de secours
│   ├── ErrorMessage.jsx       # Message inline
│   └── ErrorPage.jsx          # Pages d'erreur
├── hooks/
│   ├── useErrorHandler.js     # Hook de gestion d'erreurs
│   └── useApiError.js         # Hook pour erreurs API
└── schemas/
    ├── authSchemas.js         # Validation auth
    └── professionalSchemas.js # Validation professionnels
```

---

## Composants

### ErrorBoundary
Attrape les erreurs React non gérées et affiche un UI de secours.

```jsx
import ErrorBoundary from './components/errors/ErrorBoundary';

function App() {
  return (
    <ErrorBoundary>
      <YourApp />
    </ErrorBoundary>
  );
}
```

**Props:**
- `fallback`: Composant de secours personnalisé
- `onError`: Callback appelé lors d'une erreur
- `onReset`: Callback appelé lors de la réinitialisation

### ErrorPage
Page d'erreur élégante pour 404, 500, etc.

```jsx
import ErrorPage from './components/errors/ErrorPage';

// Page 404
<Route path="*" element={<ErrorPage code={404} />} />

// Page 500
<ErrorPage 
  code={500}
  title="Erreur serveur"
  message="Une erreur s'est produite"
/>
```

### ErrorMessage
Affiche un message d'erreur inline.

```jsx
import ErrorMessage from './components/errors/ErrorMessage';

<ErrorMessage 
  message="Email invalide" 
  variant="error" // 'error' | 'warning' | 'info'
/>
```

## Hooks

### useErrorHandler
Hook principal pour gérer les erreurs dans les composants.

```jsx
import { useErrorHandler } from '../hooks/useErrorHandler';

function MyComponent() {
  const { handleError, showSuccess, showWarning } = useErrorHandler();

  const handleSubmit = async () => {
    try {
      await someApiCall();
      showSuccess('Opération réussie !');
    } catch (error) {
      handleError(error);
    }
  };
}
```

**Méthodes disponibles:**
- `handleError(error, options)` - Gère une erreur
- `handleValidationError(errors)` - Gère les erreurs de validation
- `handleAsync(fn, options)` - Wrapper async avec gestion d'erreur
- `showSuccess(message)` - Affiche un toast de succès
- `showInfo(message)` - Affiche un toast d'info
- `showWarning(message)` - Affiche un toast d'avertissement

### useApiError
Hook avec gestion d'états pour les appels API.

```jsx
import { useApiError } from '../hooks/useApiError';

function MyComponent() {
  const { isLoading, error, data, execute } = useApiError();

  const fetchData = async () => {
    const [result, err] = await execute(
      () => api.get('/endpoint'),
      {
        successMessage: 'Données chargées !',
        onSuccess: (data) => console.log(data),
      }
    );
  };

  return (
    <div>
      {isLoading && <p>Chargement...</p>}
      {error && <p>Erreur: {error.message}</p>}
      {data && <p>Données: {data}</p>}
    </div>
  );
}
```

## Utilitaires

### errorHandler
Service centralisé de gestion d'erreurs.

```js
import errorHandler from '../utils/errorHandler';

// Gérer une erreur
errorHandler.handle(error, {
  showToast: true,
  logError: true,
  context: { userId: '123' },
});

// Gérer les erreurs de validation
errorHandler.handleValidationError({
  email: ['Email invalide'],
  password: ['Mot de passe trop court'],
});
```

### logger
Service de logging pour développement et production.

```js
import logger from '../utils/logger';

logger.info('Information', { data });
logger.error('Erreur', error, { context });
logger.warn('Avertissement', data);
logger.debug('Debug', data);

// Logs API
logger.apiRequest('POST', '/api/login', data);
logger.apiResponse('POST', '/api/login', 200, response);
logger.apiError('POST', '/api/login', error);
```

### apiClient
Instance Axios configurée avec intercepteurs.

```js
import { api } from '../utils/apiClient';

// Appels API
const data = await api.get('/users');
const created = await api.post('/users', userData);
const updated = await api.put('/users/1', userData);
await api.delete('/users/1');

// Upload de fichier
await api.upload('/upload', file, (progress) => {
  console.log(progress);
});
```

## Schémas de validation

### Avec React Hook Form + Zod

```jsx
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { loginSchema } from '../schemas/authSchemas';

function LoginForm() {
  const {
    register,
    handleSubmit,
    formState: { errors, isSubmitting },
  } = useForm({
    resolver: zodResolver(loginSchema),
  });

  const onSubmit = async (data) => {
    // Les données sont déjà validées ici
    console.log(data);
  };

  return (
    <form onSubmit={handleSubmit(onSubmit)}>
      <input {...register('email')} />
      {errors.email && <span>{errors.email.message}</span>}
      
      <input type="password" {...register('password')} />
      {errors.password && <span>{errors.password.message}</span>}
      
      <button disabled={isSubmitting}>Se connecter</button>
    </form>
  );
}
```

## Exemples d'utilisation

### Exemple complet - Page de connexion

```jsx
import React from 'react';
import { useNavigate } from 'react-router-dom';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { loginSchema } from '../schemas/authSchemas';
import { useErrorHandler } from '../hooks/useErrorHandler';
import { authApi } from '../utils/apiClient';

const LoginPage = () => {
  const navigate = useNavigate();
  const { showSuccess, handleError } = useErrorHandler();
  
  const {
    register,
    handleSubmit,
    formState: { errors, isSubmitting },
  } = useForm({
    resolver: zodResolver(loginSchema),
  });

  const onSubmit = async (data) => {
    try {
      const response = await authApi.login(data);
      showSuccess('Connexion réussie !');
      navigate('/dashboard');
    } catch (error) {
      handleError(error);
    }
  };

  return (
    <form onSubmit={handleSubmit(onSubmit)}>
      <input {...register('email')} />
      {errors.email && <span>{errors.email.message}</span>}
      
      <input type="password" {...register('password')} />
      {errors.password && <span>{errors.password.message}</span>}
      
      <button disabled={isSubmitting}>
        {isSubmitting ? 'Connexion...' : 'Se connecter'}
      </button>
    </form>
  );
};
```

### Exemple - Appel API avec useApiError

```jsx
import { useApiError } from '../hooks/useApiError';
import { api } from '../utils/apiClient';

function ProductList() {
  const { isLoading, error, data, execute } = useApiError();

  useEffect(() => {
    execute(
      () => api.get('/products'),
      { successMessage: 'Produits chargés' }
    );
  }, []);

  if (isLoading) return <Loader />;
  if (error) return <ErrorMessage message={error.message} />;
  
  return (
    <div>
      {data?.products.map(product => (
        <ProductCard key={product.id} {...product} />
      ))}
    </div>
  );
}
```


### Logger les erreurs importantes
```jsx
try {
  await criticalOperation();
} catch (error) {
  logger.error('Critical operation failed', error, {
    userId: user.id,
    timestamp: Date.now(),
  });
  handleError(error);
}
```

### Fournir du contexte aux erreurs
```jsx
const { handleError } = useErrorHandler({
  context: { 
    page: 'RegisterPage',
    userId: currentUser?.id 
  }
});
```

### Utiliser ErrorBoundary pour isoler les sections
```jsx
// Isoler chaque section majeure
<ErrorBoundary name="Dashboard">
  <Dashboard />
</ErrorBoundary>

<ErrorBoundary name="Sidebar">
  <Sidebar />
</ErrorBoundary>
```

## Configuration

### Variables d'environnement

Créez un fichier `.env` dans le dossier `frontend/`:

```env
VITE_API_BASE_URL=http://localhost:8080/api
```
