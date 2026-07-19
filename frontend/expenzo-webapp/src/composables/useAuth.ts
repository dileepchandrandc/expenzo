import { ref } from 'vue';
import { useRouter } from 'vue-router';
import { loginApi } from '../api/auth';
import type { CreateUserRequest } from '../api/user';
import { createUserApi } from '../api/user';

interface SignupData {
  email: string;
  password: string;
  firstName: string;
  lastName?: string;
  countryCode?: string;
  mobileNumber?: string;
}

const accessToken = ref<string | null>(localStorage.getItem('accessToken'));
const refreshToken = ref<string | null>(localStorage.getItem('refreshToken'));
const userId = ref<string | null>(localStorage.getItem('userId'));

export function useAuth() {
  const router = useRouter();

  const isAuthenticated = () => {
    return !!accessToken.value;
  };

  const login = async (email: string, password: string) => {
    const response = await loginApi({ email, password });

    const { accessToken: at, refreshToken: rt } = response.data;

    localStorage.setItem('accessToken', at);
    localStorage.setItem('refreshToken', rt);

    accessToken.value = at;
    refreshToken.value = rt;

    return response.data;
  };

  const signup = async (data: SignupData) => {
    const payload: CreateUserRequest = {
      email: data.email,
      password: data.password,
      firstName: data.firstName,
      lastName: data.lastName || undefined,
      countryCode: data.countryCode || undefined,
      mobileNumber: data.mobileNumber || undefined,
    };

    await createUserApi(payload);

    // Auto-login after successful signup
    await login(data.email, data.password);
  };

  const logout = () => {
    localStorage.removeItem('accessToken');
    localStorage.removeItem('refreshToken');
    localStorage.removeItem('userId');

    accessToken.value = null;
    refreshToken.value = null;
    userId.value = null;

    router.push('/login');
  };

  return {
    accessToken,
    refreshToken,
    userId,
    isAuthenticated,
    login,
    signup,
    logout,
  };
}
